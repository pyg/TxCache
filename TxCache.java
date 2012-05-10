import java.util.*;

public class TxCache {
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
	private boolean caching;
	private List<Pin> pinset;
	private List<Pin> pincushion;
	private int current_time;
	
	private TransactionType tran_type;
	
	private TxCache() {
		caching = true;
		pinset = new ArrayList<Pin>();
		pincushion = new LinkedList<Pin>();
	}
	
	public static TxCache getTxCache() {
		if (txCache == null) txCache = new TxCache();
		return txCache;
	}

	public void BEGIN_RO(int staleness) {
		if (!caching) return; // **Do nothing
		// **DON'T tell the DB to start RO, as we don't know the snapshot ID before checking the cache
		
		tran_type = TransactionType.READONLY;
		// **No need to tell the Cache Server to begin-RO, currently
		pinset.clear();
		for (Pin pin : pincushion) if (pin.getTimestamp() + staleness >= current_time) {
			pin.useIt();
			pinset.add(pin);
		}
	}

	public void BEGIN_RW() {
		//if (!caching) retunr;
		//tell the DB to start RW
		//tran_type = READWRITE;
		//if (!caching) return;
		//tell the Cache Server to start RW
	}

	public void COMMIT() {
		//tell the DB to COMMIT
		//tran_type = NOTRANSACTION;
		//if (!caching) return;
		//get the invalidation tags
		//tell the Cache Server to COMMIT(tags)
		//if (tran_type == READONLY) for (pin in pinset) --pin.using;
	}

	public void ABORT() {
		//tell the DB to ABORT
		//tran_type = NOTRANSACTION;	
		//if (!caching) return;
		//tell the Cache Server to ABORT
		//if (tran_type == READONLY) for (pin in pinset) --pin.using;
	}

	public void PQexecWrapper() {
		//PQexec(...);
		//if (tran_type == READONLY) {
		//	update interval
		//	tagset.add(tags);
		//}
	}

	public void UNPIN(int snapshotid) {
		//tell the DB to UNPIN(snapshotid);
	}

	public void some_wrapper() {
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
//const int MAX_SNAPSHOT_NUM = 10;


//synchronized PinCushion pincushion;

//TagSet tags;
//Interval interval;



int main() {
	//functions
}
*/
