javac TxCache.java
javah -jni TxCache
g++ proxy.c -shared -fpic -o libproxy.dylib -I/System/Library/Frameworks/JavaVM.framework/Versions/A/Headers/ -I/opt/local/include/postgresql91/ -L/opt/local/lib/postgresql91/ -lpq

#java -Djava.library.path=. ConnectDB
