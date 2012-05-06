#include <iostream>
#include <sstream>
#include "Memo.h"
#include "boost/tuple/tuple.hpp"
#include "boost/tuple/tuple_comparison.hpp"
#include "boost/tuple/tuple_io.hpp"

using namespace std;

struct C {
    int three(int x) { 
        cout << "Called three(" << x << ")" << endl;
        int i;
        for ( i = 0; i < 1000000000; ++i ) {
        	
        }
        return 3;
    }

    double square(float x) {
        cout << "Called square(" << x << ")" << endl;
        return x * x;
    }
};

int main(void) {
    C       c;
    Memo<C> m(c);

    cout << m.dispatch(&C::three, 1) << endl;
    cout << m.dispatch(&C::three, 2) << endl;
    cout << m.dispatch(&C::three, 1) << endl;
    cout << m.dispatch(&C::three, 2) << endl;

    cout << m.dispatch(&C::square, 2.3f) << endl;
    cout << m.dispatch(&C::square, 2.3f) << endl;

    return 0;
}