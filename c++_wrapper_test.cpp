#include<stdio.h>
#include<stdarg.h>
#include<iostream>
using namespace std;

void foo(...) {
	cout << "it works: in foo" << endl;
}

class function {
public:
	void make_cacheable(void (*f1)(...)) {
		fn = f1;
	}
	bool operator() (...) {
		va_list ap;
/*		char type[100];

		va_start(ap,arg_num);
		while(arg_num > 0) {
			va_arg(ap,type);
			cout << type << endl;
			arg_num--;
		}
		*/
		//cout << ap << endl;

		fn();
		va_end(ap);
	}

	void (*fn)(...);
};
int main() {
//	foo(1,2);
	function f1;
	f1.make_cacheable(&foo);
	f1(2,1,2);
	return 0;
}
