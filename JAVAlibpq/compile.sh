javac ConnectDB.java
javah -jni ConnectDB
g++ proxy.c -shared -fpic -o libproxy.so -I/usr/lib/jvm/jdk1.7.0_03/include/ -I/usr/lib/jvm/jdk1.7.0_03/include/linux/ -I/usr/local/pgsql/include -lpq

#java -Djava.library.path=. ConnectDB
