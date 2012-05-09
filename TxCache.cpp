#include <cstdio>

//const bool caching = true;
//const int MAX_SNAPSHOT_NUM = 10;

//TransactionType tran_type;
//synchronized PinCushion pincushion;
//PinSet pinset;
//TagSet tags;
//Interval interval;

void BEGIN-RO(TxTime staleness) {
	// **DON'T tell the DB to start RO, as we don't know the snapshot ID
	//if (!caching) return;
	//tran_type = READONLY;
	//tell the Cache Server to start RO
	//clear pinset;
	//for (pin in pincushion) if (pin.timestamp + staleness >= current_time) {
	//	pin.using++;
	//	pinset.add(pin);
	//}
}

void BEGIN-RW() {
	//if (!caching) retunr;
	//tell the DB to start RW
	//tran_type = READWRITE;
	//if (!caching) return;
	//tell the Cache Server to start RW
}

void COMMIT() {
	//tell the DB to COMMIT
	//tran_type = NOTRANSACTION;
	//if (!caching) return;
	//get the invalidation tags
	//tell the Cache Server to COMMIT(tags)
	//if (tran_type == READONLY) for (pin in pinset) --pin.using;
}

void ABORT() {
	//tell the DB to ABORT
	//tran_type = NOTRANSACTION;	
	//if (!caching) return;
	//tell the Cache Server to ABORT
	//if (tran_type == READONLY) for (pin in pinset) --pin.using;
}

void PQexecWrapper() {
	//PQexec(...);
	//if (tran_type == READONLY) {
	//	update interval
	//	tagset.add(tags);
	//}
}

void UNPIN(int snapshotid) {
	//tell the DB to UNPIN(snapshotid);
}

void some_wrapper() {
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

int main() {
	//functions
}

