#include <cstdio>

//const bool caching = true;

//TransactionType tran_type;
//PinCushion pincushion;
//PinSet pinset;

void BEGIN-RO(TxTime staleness) {
	//tell the DB to start RO
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
	//if (tran_type == READONLY) {
	//	add to cache;
	//}
	//PQexec(...);
}

void some_wrapper() {
	//if (tran_type == READONLY) {
	//	if (hit cache) return value;
	//}
	//else invoke the original function;
}

int main() {
	//functions
}

