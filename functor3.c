// REQUIRES g++ 4.7 OR LATER. COMPILE WITH g++ --std=c++0x ./functor3.c -o a.out

#include <functional>
#include <unordered_map>
#include <memory>
#include <map>
template <typename ReturnType, typename... Args>
std::function<ReturnType (Args...)>
memoize(ReturnType (*func) (Args...))
{
  auto cache = std::make_shared<std::map<std::tuple<Args...>, ReturnType>>();
  return ([=](Args... args) mutable  {
          std::tuple<Args...> t(args...);
          if (cache->find(t) == cache->end())
              (*cache)[t] = func(args...);
          return (*cache)[t];
  });
}
template <typename F_ret, typename...  F_args>
std::function<F_ret (F_args...)>
memoized_recursion(F_ret (*func)(F_args...))
{
  typedef std::function<F_ret (F_args...)> FunctionType;
  static std::unordered_map<decltype(func), FunctionType> functor_map;
 
  if(functor_map.find(func) == functor_map.end())
    functor_map[func] = memoize(func);
 
  return functor_map[func];
}
 
#define STATIC_MEMOIZER(func) static_memoizer<decltype(&func), &func>
#include <stdio.h>

unsigned long fibonacci(unsigned n, char a)
{
  return (n < 2) ? n :
       memoized_recursion(fibonacci)(n - 1, 'a') +
       memoized_recursion(fibonacci)(n - 2, 'a');
       //fibonacci(n-1) + fibonacci(n-2);
}

int main (void) {
    long u = memoized_recursion(fibonacci)(400, 'a');
	printf("%u\n",u);
}