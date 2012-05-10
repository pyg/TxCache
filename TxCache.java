import java.util.*;

public class TxCache {
	final int CONNECTION_OK = 0;
	final int PGRES_COMMAND_OK = 1;
	final String INITIALIZATION_SUCCESS = "SUCCESS";
	final int MAX_NUMBER_OF_CONNECTIONS = 10;

	public native String UMASSPQinitialize();
	
    public native String PQconnectdb(String conninfo);
    public native int PQstatus(String conn);
	public native String PQerrorMessage(String conn);
	public native String PQexec(String conn, String sql);
	public native int PQresultStatus(String res);
	public native void PQclear(String res);
	public native String PQcmdStatus(String res);
	public native void PQfinish(String conn);
	public native int PQntuples(String res);
	public native int PQnfields(String res);
	public native String PQgetvalue(String res, int i, int j);


    static {
        System.loadLibrary("proxy");
    }

	final int MAX_SNAPSHOT_NUM = 10;
	
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
	
	private static TxCache txCache = null;
	
	private Map<String, Boolean> caching;
	private Map<String, List<Pin>> pinset;
	private Map<String, List<Pin>> pincushion;
	private Map<String, Integer> current_time;
	
	private Map<String, TransactionType> tran_type;
	
	private TxCache() {
		caching = new Map<String, Boolean>();
		pinset = new Map<String, List<Pin>>();
		pincushion = new Map<String, List<Pin>>();
		current_time = new Map<String, Integer>();
		
		String init = UMASSPQinitialize();
		if (!init.equals(INITIALIZATION_SUCCESS)) {
			System.err.println("DB Proxy Initialization Error");
			System.exit(1);
		}
		else System.out.println("Initialization: " + init);
	}
	
	public static TxCache getTxCache() {
		if (txCache == null) txCache = new TxCache();
		return txCache;
	}

	public void BEGIN_RO(String conn, int staleness) {
		if (!cache.containsKey(conn)) return; // **No such connection
		if (!caching.get(conn)) { // **Do nothing
			PQexecWrapper(conn, "BEGIN READ ONLY ISOLATION LEVEL SERIALIZABLE;");
			return;
		}
		
		// **DON'T tell the DB to start RO, as we don't know the snapshot ID before checking the cache
		
		tran_type.put(conn, TransactionType.READONLY);
		// **No need to tell the Cache Server to begin-RO, currently
		pinset.get(conn).clear();
		for (Pin pin : pincushion.get(conn)) if (pin.getTimestamp() + staleness >= current_time) {
			pin.useIt();
			pinset.get(conn).add(pin);
		}
	}

	public void BEGIN_RW(String conn) {
		if (!cache.containsKey(conn)) return; // **No such connection

		// **tell the DB to start RW		
		PQexecWrapper(conn, "BEGIN ISOLATION LEVEL SERIALIZABLE;");
		tran_type.put(conn, TransactionType.READWRITE);
		// **No need to tell the Cache Server to start RW
	}

	public void COMMIT(String conn) {
		if (!cache.containsKey(conn)) return; // **No such connection
		
		// **tell the DB to COMMIT
		PQexecWrapper(conn, "COMMIT;");
		
		if (!caching.get(conn)) { // **Do nothing
			tran_type.put(conn, TransactionType.NON);
			return;
		}
		//get the invalidation tags
		//tell the Cache Server to COMMIT(tags)
		//if (tran_type == READONLY) for (pin in pinset) --pin.using;
		tran_type.put(conn, TransactionType.NON);
	}

	public void ABORT(String conn) {
		if (!cache.containsKey(conn)) return; // **No such connection
		
		// **tell the DB to ABORT
		PQexecWrapper(conn, "ABORT;");
		
		if (!caching.get(conn)) { // **Do nothing
			tran_type.put(conn, TransactionType.NON);
			return;
		}
		// ** No need to tell the Cache Server to ABORT
		//if (tran_type == READONLY) for (pin in pinset) --pin.using;
		tran_type.put(conn, TransactionType.NON);
	}

	public void PQexecWrapper(String conn, String sqlstmt) {
		if (!cache.containsKey(conn)) return; // **No such connection
		
		PQexec(conn, sqlstmt);
		if (!caching.get(conn)) return // **Do nothing
		//if (tran_type == READONLY) {
		//	update interval
		//	tagset.add(tags);
		//}
	}

	public void UNPIN(int snapshotid) {
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
