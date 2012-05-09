#include <cstdio>

//const bool caching = true;

//TransactionType tran_type;
//PinCushion pincushion;
//PinSet pinset;
//TagSet tags;
//Interval interval;

void BEGIN-RO(TxTime staleness) {
	//DON'T tell the DB to start RO, as we don't know the snapshot ID
	//if (!caching) return;
	//tran_type = READONLY;
	//tell the Cache Server to start RO
	//clear pinset;
	//for (pin in pincushion) if (pin.timestamp + staleness >= current_time) pinset.add(pin);
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
	//tell the Cache Server to COMMIT
}

void ABORT() {
	//tell the DB to ABORT
	//tran_type = NOTRANSACTION;	
	//if (!caching) return;
	//tell the Cache Server to ABORT
}

void PQexecWrapper() {
	//PQexec(...);
	//if (tran_type == READONLY) {
	//	update interval
	//	tagset.add(tags);
	//}
}

void some_wrapper() {
	//if (tran_type == READONLY) {
	//	look up cache and update pinset
	//	if (hit) return value;
	//	else {
	//		interval = (-oo, +oo)
	//		clear tagset;
	//		id = select a snapshot from pinset
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

