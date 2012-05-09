#include <functional>
#include <unordered_map>
#include <memory>
#include <map>
//#include <multimap>
#include <set>
#include <stdio.h>
#include <thread>
#include <iostream>
#include <chrono>
#include <unistd.h>

#include <string.h>
#include <string>

using namespace std;
template <typename Ret, typename...  Args>
Ret
memo(Ret (*func)(Args...), Args... args)
{
	static std::map<std::tuple<decltype(func),Args...>, Ret> cache;
	std::tuple<decltype(func),Args...> key = std::make_tuple(func,args...);
	//auto key = std::make_tuple(func,args...);
	Ret temp;
	//printf("before: %d\n",cache[key]);
	if(cache.find(key) == cache.end()) {
		temp = func(args...);
		//cacrhe[key] = temp;
		cache.insert (std::pair<std::tuple<decltype(func),Args...>, Ret>(key,temp) );
		printf("size: %d\n",cache.size());
	} else {
		temp = cache[key];
		printf("size: %d\n",cache.size());
	}
	//std::chrono::milliseconds dura(500);
	
	//sleep(1);
	//printf("after: %s\n",key);
	return temp;
//	return temp;
}

struct A {
public:
int a;
int b;
};

bool operator<(const A& lhs, const A& rhs) { return memcmp(&lhs, &rhs, sizeof(struct A)); }

int runStruct(struct A x, int i) {
//int i = 0;
while (i < 1000000000) i++;
printf("computed: %d\n",x.a+x.b);
return x.a + x.b;
}

int runStruct2(struct A x, int i) {
//int i = 0;
while (i < 1000000000) i++;
printf("computed: %d\n",x.a+x.b);
return x.a + x.b;
}

unsigned long fibonacci2(int n, char a)
{
  return (n < 2) ? n :
       //memoized_recursion(fibonacci)(n - 1, 'a') +
       //memoized_recursion(fibonacci)(n - 2, 'a');
       fibonacci2(n-1,'a') + fibonacci2(n-2,'a');
}

/*
int runStruct(struct A x) {
int i = 0;
while (i < 1000000000) i++;
printf("computed: %d\n",x.a+x.b);
return x.a + x.b;
}
*/

int main() {
	struct A a;
	a.a = 10;
	a.b = 20;
	
	struct A b;
	b.a = 10;
	b.b = 30;
// 	int somthin = memo(runStruct,a,2);
// 	printf("%d\n",somthin);
	//a.b = 30;
	int somthi2n = memo(runStruct,a,2);
	printf("%d\n",somthi2n);
	//a.b = 20;
		
		/*
	printf("%d\n",memo(fibonacci2,39, 'a'));
	
	printf("%d\n",memo(fibonacci2,40, 'a'));
	printf("%d\n",memo(fibonacci2,39, 'a'));
	*/
	
	int somthi3n = memo(runStruct,b,2);
	printf("%d\n",somthi3n);
	
// //	a.b = 30;
int somthi4n = memo(runStruct,a,2);
 	printf("%d\n",somthi4n);
	//a.b = 20;
	int somthi5n = memo(runStruct,b,2);
	printf("%d\n",somthi5n);
	
}