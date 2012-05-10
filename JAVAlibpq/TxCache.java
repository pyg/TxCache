import java.util.*;

public class TxCache {
	static final int CONNECTION_OK = 0;
	static final int PGRES_COMMAND_OK = 1;
	static final String INITIALIZATION_SUCCESS = "SUCCESS";
	static final int MAX_NUMBER_OF_CONNECTIONS = 10;

	private static native String UMASSPQinitialize();
	
    private static native String PQconnectdb(String conninfo);
    private static native int PQstatus(String conn);
	private static native String PQerrorMessage(String conn);
	private static native String PQexec(String conn, String sql);
	private static native int PQresultStatus(String res);
	private static native void PQclear(String res);
	private static native String PQcmdStatus(String res);
	private static native void PQfinish(String conn);
	private static native int PQntuples(String res);
	private static native int PQnfields(String res);
	private static native String PQgetvalue(String res, int i, int j);


    static {
        System.loadLibrary("proxy");
    }

	static final int MAX_SNAPSHOT_NUM = 10;
	
	public enum TransactionType {
		READONLY, READWRITE, NON
	}
	
	class Pin {
		private int id; //which is also timestamp;
		private int users;
		public Pin(int id) {
			this.id = id;
			this.users = 0;
		}
		public void setId(int id) {
			this.id = id;
		}
		public int getId() {
			return this.id;
		}
		public void useIt() {
			++users;
		}
		public void releaseIt() {
			if (users > 0) --users;
		}
		public int countUsers() {
			return users;
		}
		public int getTimestamp() {
			return id;
		}
	}
	
	private static int number_of_connections;
	
	private static Map<String, Boolean> caching;
	private static Map<String, List<Pin>> pinset;
	private static Map<String, List<Pin>> pincushion;
	private static Map<String, Integer> current_time;
	
	private static Map<String, TransactionType> tran_type;
	
	
	
	public static void initializeTxCache() {
		number_of_connections = 0;
		
		caching = new HashMap<String, Boolean>();
		pinset = new HashMap<String, List<Pin>>();
		pincushion = new HashMap<String, List<Pin>>();
		current_time = new HashMap<String, Integer>();
		
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
	}

	public static void BEGIN_RW(String conn) {
		if (!caching.containsKey(conn)) return; // **No such connection

		// **tell the DB to start RW		
		TxPQexec(conn, "BEGIN ISOLATION LEVEL SERIALIZABLE;");
		tran_type.put(conn, TransactionType.READWRITE);
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
		//if (tran_type == READONLY) for (pin in pinset) --pin.using;
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
		//if (tran_type == READONLY) for (pin in pinset) --pin.using;
		tran_type.put(conn, TransactionType.NON);
	}


/*
	A()
	B()
	PQexec()
	
	wrap(A)
	wrap(B)
	wrap(PQexec, arg) //invoke TxCache.PQexecWrapper()..... (conn, "select *")
*/
	
	public static void TxPQexec(String conn, String sqlstmt) {
		if (!caching.containsKey(conn)) return; // **No such connection
		
		PQexec(conn, sqlstmt);
		if (!caching.get(conn)) return; // **Do nothing
		//if (tran_type == READONLY) {
		//	update interval
		//	tagset.add(tags);
		//}
	}
	
	public static String TxPQconnectdb(String conninfo, Boolean doCache) {
		if (number_of_connections == MAX_NUMBER_OF_CONNECTIONS) return "!TOO MANY CONNECTIONS!";
		
		String conn = PQconnectdb(conninfo);
		caching.put(conn, doCache);
		pinset.put(conn, new ArrayList<Pin>());
		pincushion.put(conn, new LinkedList<Pin>());
		current_time.put(conn, 0);
		tran_type.put(conn, TransactionType.NON);
		
		return conn;
	}
	
	public static void TxPQfinish(String conn) {
		if (!caching.containsKey(conn)) return; // **No such connection
		
		caching.remove(conn);
		pinset.remove(conn);
		pincushion.remove(conn);
		current_time.remove(conn);
		tran_type.put(conn, TransactionType.NON);
		
		PQfinish(conn);
	}

	public static void UNPIN(int snapshotid) {
		//tell the DB to UNPIN(snapshotid);
	}

	public void some_wrapper() { // It should be in user program and outside TxCache.
		//if (tran_type == READONLY) {
		//	look up cache and update pinset (and --pin.using!)
		//	if (hit) return value;
		//	else {
		//		interval = (-oo, +oo)
		//		clear tagset;
		//		id = select a snapshot from pinset
		//		if (id == *) {
		//			if (pin.length == MAX_SNAPSHOT_NUM) {
		//				// **Don't have to wait because the number of snapshots should always be less than the number of connections
		//				// **We should just make sure that MAX_SNAPSHOT_NUM < max # of connections
		//				for (pin in pincushion) if (pin.using == 0) {
		//					UNPIN(pin.id);
		//					break;
		//				}
		//			}
		//			Pin a new snapshot;
		//			id = new snapshot's id;
		//			pincushion.add(id);
		//		}
		//		tell the DB to begin ro with snapshot id
		//		invoke the original function;
		//		add the value with its interval and tags into cache
		//	}
		//}
		//else invoke the original function;
	}
}

/*



//synchronized PinCushion pincushion;

//TagSet tags;
//Interval interval;



int main() {
	//functions
}
*/
