#include <algorithm>
#include <map>
#include <utility>
#include <vector>

// An anonymous namespace to hold a search predicate definition. Users of
// Memo don't need to know this implementation detail, so I keep it
// anonymous. I use a predicate to search a vector of pairs instead of a
// simple map because a map requires that operator< be defined for its key
// type, and operator< isn't defined for member function pointers, but
// operator== is.
namespace {
    template <typename Type1, typename Type2>
    class FirstEq {
        Type1 value;

    public:
        typedef std::pair<Type1, Type2> ArgType;

        FirstEq(Type1 t) : value(t) {}

        bool operator()(const ArgType& rhs) const { 
            return value == rhs.first;
        }
    };
};

template <typename T>
class Memo {
    // Typedef for a member function of T. The C++ standard allows casting a
    // member function of a class with one signature to a type of another
    // member function of the class with a possibly different signature. You
    // aren't guaranteed to be able to call the member function after
    // casting, but you can use the pointer for comparisons, which is all we
    // need to do.
    typedef void (T::*TMemFun)(void);

    typedef std::vector< std::pair<TMemFun, void*> > FuncRecords;

    T           memoized;
    FuncRecords funcCalls;

public:
    Memo(T t) : memoized(t) {}

    template <typename ReturnType, typename ArgType>
    ReturnType dispatch(ReturnType (T::* memFun)(ArgType), ArgType arg) { // Supports one param

        typedef std::map<ArgType, ReturnType> Record;

        // Look up memFun in the record of previously invoked member
        // functions. If this is the first invocation, create a new record.
        typename FuncRecords::iterator recIter = 
            find_if(funcCalls.begin(),
                    funcCalls.end(),
                    FirstEq<TMemFun, void*>(
                        reinterpret_cast<TMemFun>(memFun)));

        if (recIter == funcCalls.end()) {
            funcCalls.push_back(
                std::make_pair(reinterpret_cast<TMemFun>(memFun),
                               static_cast<void*>(new Record)));
            recIter = --funcCalls.end();
        }

        // Get the record of previous arguments and return values.
        // Find the previously calculated value, or calculate it if
        // necessary.
        Record*                   rec      = static_cast<Record*>(
                                                 recIter->second);
        typename Record::iterator callIter = rec->lower_bound(arg);

        if (callIter == rec->end() || callIter->first != arg) {
            callIter = rec->insert(callIter,
                                   std::make_pair(arg,
                                                  (memoized.*memFun)(arg)));
        }
        return callIter->second;
    }
};