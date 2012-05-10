import java.util.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.*;

public class TxCache {
	static final int CONNECTION_OK = 0;
	static final int PGRES_COMMAND_OK = 1;
	static final String INITIALIZATION_SUCCESS = "SUCCESS";
	static final int MAX_NUMBER_OF_CONNECTIONS = 10;
	static final int MAX_NUMBER_OF_SNAPSHOTS = 10;

    
    static {
        System.loadLibrary("proxy");
    }
    
	private static native String UMASSPQinitialize();
	
    private static native String PQconnectdb(String conninfo);
    public static native int PQstatus(String conn);
	public static native String PQerrorMessage(String conn);
	private static native String PQexec(String conn, String sql);
	public static native int PQresultStatus(String res);
	public static native void PQclear(String res);
	public static native String PQcmdStatus(String res);
	private static native void PQfinish(String conn);
	public static native int PQntuples(String res);
	public static native int PQnfields(String res);
	public static native String PQgetvalue(String res, int i, int j);




	static final int MAX_SNAPSHOT_NUM = 10;
	
	public enum TransactionType {
		READONLY, READWRITE, NON
	}
	
	private static int number_of_connections;
	private static Pin RecentPin;
	
	// connection relevant
	private static Map<String, Boolean> caching;
	private static Map<String, List<Pin>> pincushion;
	private static Map<String, Integer> current_time;
	
	// transaction relevant
	private static Map<String, Map<String, Integer>> invalidations; //READWRITE
	private static Map<String, List<Pin>> pinset; //READONLY
	private static Map<String, List<String>> tagsets; //READONLY
	private static Map<String, IntPair> intervals; //READONLY
	
	private static Map<String, TransactionType> tran_type;
	
	
	
	public static void initializeTxCache() {
		number_of_connections = 0;
		RecentPin = new Pin(-1);
		
		caching = new HashMap<String, Boolean>();
		pinset = new HashMap<String, List<Pin>>();
		pincushion = new HashMap<String, List<Pin>>();
		current_time = new HashMap<String, Integer>();
		tran_type = new HashMap<String, TransactionType>();
		invalidations = new HashMap<String, Map<String, Integer>>();
		tagsets = new HashMap<String, List<String>>();
		intervals = new HashMap<String, IntPair>();
        
		String init = UMASSPQinitialize();
		if (!init.equals(INITIALIZATION_SUCCESS)) {
			System.err.println("DB Proxy Initialization Error");
			System.exit(1);
		}
		else System.out.println("Initialization: " + init);
	}
	
	public static void BEGIN_RO(String conn, int staleness) {
		if (!caching.containsKey(conn)) return; // **No such connection
		if (!caching.get(conn)) { // **Do nothing
			TxPQexec(conn, "BEGIN READ ONLY ISOLATION LEVEL SERIALIZABLE;");
			return;
		}
		
		// **DON'T tell the DB to start RO, as we don't know the snapshot ID before checking the cache
		
		tran_type.put(conn, TransactionType.READONLY);
		// **No need to tell the Cache Server to begin-RO, currently
		pinset.get(conn).clear();
		for (Pin pin : pincushion.get(conn)) if (pin.getTimestamp() + staleness >= current_time.get(conn)) {
			pin.useIt();
			pinset.get(conn).add(pin);
		}
		pinset.get(conn).add(RecentPin);
		tagsets.get(conn).clear();
		(intervals.get(conn)).first = -1;
		intervals.get(conn).second = -1;
	}

	public static void BEGIN_RW(String conn) {
		if (!caching.containsKey(conn)) return; // **No such connection

		// **tell the DB to start RW		
		TxPQexec(conn, "BEGIN ISOLATION LEVEL SERIALIZABLE;");
		tran_type.put(conn, TransactionType.READWRITE);
		invalidations.get(conn).clear();
		// **No need to tell the Cache Server to start RW
	}

	public static void COMMIT(String conn) {
		if (!caching.containsKey(conn)) return; // **No such connection
		
		// **tell the DB to COMMIT
		TxPQexec(conn, "COMMIT;");
		
		if (!caching.get(conn)) { // **Do nothing
			tran_type.put(conn, TransactionType.NON);
			return;
		}
		
		//get the invalidation tags
		//tell the Cache Server to COMMIT(tags)
		
		if (tran_type.get(conn) == TransactionType.READONLY)
			for (Pin pin : pinset.get(conn)) pin.releaseIt();
//			tagsets.get(conn).clear();
//			intervals.get(conn).first = -1;
//			intervals.get(conn).second = -1;
//		}
//		else if (tran_type.get(conn) == TransactionType.READONLY) {		
//		}
		tran_type.put(conn, TransactionType.NON);
	}

	public static void ABORT(String conn) {
		if (!caching.containsKey(conn)) return; // **No such connection
		
		// **tell the DB to ABORT
		TxPQexec(conn, "ABORT;");
		
		if (!caching.get(conn)) { // **Do nothing
			tran_type.put(conn, TransactionType.NON);
			return;
		}
		// ** No need to tell the Cache Server to ABORT
		if (tran_type.get(conn) == TransactionType.READONLY) 
			for (Pin pin : pinset.get(conn)) pin.releaseIt();
		tran_type.put(conn, TransactionType.NON);
	}

	public static String TxPQexec(String conn, String sqlstmt) {
		if (!caching.containsKey(conn)) return null; // **No such connection
		
        //System.out.println("DEBUG" + tran_type.get(conn));
		String res = PQexec(conn, sqlstmt);
		if (!caching.get(conn)) return res; // **Do nothing
		if (tran_type.get(conn) == TransactionType.READONLY) { // **user should guarantee that the sql is a "SELECT"
			// **the return looks like: "SELECT VALIDITY 133 134 TAGS 1 4001:"
			String ret = PQcmdStatus(res);
			String []sts = ret.toUpperCase().split("");
			int len = sts.length;
			int i = 0;
			int begin = -1, end = -1, tags = -1;
			for ( ; i < len; ++i) if (sts[i].equals("VALIDITY")) {
				++i;
				for ( ; i < len; ++i) if (!sts[i].equals("")) {
					try {
						begin = Integer.parseInt(sts[i]);
					}
					catch (NumberFormatException e) {
					}
					++i;
					break;
				}
				if (begin == -1) break;
				for ( ; i < len; ++i) if (!sts[i].equals("")) {
					try {
						end = Integer.parseInt(sts[i]);
					}
					catch (NumberFormatException e) {
					}
					++i;
					break;
				}
				if (end == -1) break;
				for ( ; i < len; ++i) if (!sts[i].equals("")) {
					if (sts[i].equals("TAGS") && i + 1 < len) {
						try {
							tags = Integer.parseInt(sts[++i]);
						}
						catch (NumberFormatException e) {
						}
					}
					break;
				}
				if (tags == -1) break;
				List<String> tagset = tagsets.get(conn);
				for (++i; i < len; ++i) 
					if (!sts[i].equals("")) {
						tagset.add(sts[i]);
						--tags;
					}
				//if (0 != tags) BUG!
				
				// **shrink
				IntPair tmp = intervals.get(conn);
				if (begin > tmp.first) tmp.first = begin;
				if (end < tmp.second) tmp.second = end;
				
				break;
			}
		}
        return res;
	}
	
	public static String TxPQconnectdb(String conninfo, Boolean doCache) {
		if (number_of_connections == MAX_NUMBER_OF_CONNECTIONS) return "!TOO MANY CONNECTIONS!";
		
		String conn = PQconnectdb(conninfo);
		caching.put(conn, doCache);
		pinset.put(conn, new ArrayList<Pin>());
		pincushion.put(conn, new LinkedList<Pin>());
		current_time.put(conn, 0);
		tran_type.put(conn, TransactionType.NON);
		invalidations.put(conn, new HashMap<String, Integer>());
		tagsets.put(conn, new ArrayList<String>());
		intervals.put(conn, new IntPair(-1, -1));
		
		return conn;
	}
	
	public static void TxPQfinish(String conn) {
		if (!caching.containsKey(conn)) return; // **No such connection
		
		caching.remove(conn);
		pinset.remove(conn);
		pincushion.remove(conn);
		current_time.remove(conn);
		tran_type.remove(conn);
		invalidations.remove(conn);
		tagsets.remove(conn);
		intervals.remove(conn);
		
		PQfinish(conn);
	}

	public static void UNPIN(String conn, int snapshotid) {
		List<Pin> cushion = pincushion.get(conn);
		int sz = cushion.size();
		for (int i = 0; i < sz; ++i) if (cushion.get(i).getId() == snapshotid) {
			cushion.remove(i);
			break;
		}
		TxPQexec(conn, "UNPIN " + snapshotid + ";");
	}

    @SuppressWarnings("unchecked")
	public static <T> T wrap(String clazz, String method, String conn, Object... args) throws Exception { 
		// ** get the key first
        Class<?> theClass = Class.forName(clazz);
		Class<?>[] arguments = new Class<?>[args.length];
		
		String key = clazz + ".." + method;
		for (int i = 0; i < args.length; ++i) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(args[i]);
			key += ".." + baos.toString();
			//key += ".." + args[i].toString();
			arguments[i] = args[i].getClass();
		}
		key = conn + key;
        
        
        // ** Empty conn indicates that the function as a "local" funtion without interacting with DB
        if (!conn.equals("") && !caching.containsKey(conn))
        	return null;
        
        if (conn.equals("") || tran_type.get(conn) == TransactionType.READONLY) {
		//	look up cache and update pinset (and --pin.using!)
		//	if (hit) return value;
		//	else {
				Pin pin = pinset.get(conn).get(0); // **it should never be empty
				if (pin.getId() == -1) {
					if (pincushion.get(conn).size() == MAX_NUMBER_OF_SNAPSHOTS) {
						// **Don't have to wait because the number of active snapshots should always be less than the number of connections
						// **We should just make sure that MAX_SNAPSHOT_NUM <= max # of connections
						for (Pin p : pincushion.get(conn)) if (p.countUsers() == 0) {
							UNPIN(conn, p.getId());
							break;
						}
					}
					String res = TxPQexec(conn, "PIN;");
					String st = PQcmdStatus(res);
					String []sts = st.toUpperCase().split(" ");
					int len = sts.length;
					for (int i = 0; i < len; ++i) if (sts[i].equals("PIN")) {						
						if ((++i) < len) {
							try {
								int id = Integer.parseInt(sts[i]);
								pin = new Pin(id);
							}
							catch (NumberFormatException ex) {								
							}
						}
						break;
					}
					pincushion.get(conn).add(pin);
				}
				// **clear all other snapshots
				pinset.get(conn).clear();
				pinset.get(conn).add(pin);
				
				TxPQexec(conn, "BEGIN ISOLATION LEVEL SERIALIZABLE READ ONLY SNAPSHOTID " + pin.getId());
				
				Method toCall = theClass.getMethod(method, arguments);
				Object result = toCall.invoke(theClass, args);
				
				TxPQexec(conn, "COMMIT;");
				
		//		add the value with its interval and tags into cache
				return (T) result;
		//	}
		}
		else {
			Method toCall = theClass.getMethod(method, arguments);
			Object result = toCall.invoke(theClass, args);
			return (T) result;
		}
	}
	
    public static void main(String[] args) {
        //TxCache.initializeTxCache();
        String st = "SELECT VALIDITY   133 134 TAGS 1 4001:";
        String []sts = st.split(" ");
        for (String stt:sts) System.out.println(stt);
        //functions
    }
}

