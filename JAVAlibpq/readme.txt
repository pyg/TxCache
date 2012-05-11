1.Environment:
  We developed and tested this piece of software on both ubuntu and Mac platform.
  This introduction is based on ubuntu system.

2.Preparation:
  2.1 Install java
      get the java home path, in ubuntu system, it is:
      JAVA_HOME=/usr/lib/jvm/java-6-openjdk
  2.2 Install libpg:
      sudo apt-get install libpq5
      sudo apt-get install libpq-dev
  
  2.3 locate the corresponding files:
      After step 2.2, in Ubuntu, you may find the postgresql's include and lib file folder, they are:
      include: /usr/include/postgresql/          lib: /usr/include/postgresql/

3.Run program:
  3.1 Switch to the folder "JAVAlibpq"
  3.2 Run commands:
      3.2.1 javac *.java  //compile all java files
      3.2.2 javah -jni TxCache
      3.3.3 g++ proxy.c -shared -fpic -o libproxy.so  -I $JAVA_HOME/include/ -I $JAVA_HOME/include/linux/ -I/usr/include/postgresql/ -L/usr/include/postgresql/ -lpq

  Attention: Here "-I" means "include", -L means "library", so here we use java include and java lib, postgresql include and lib folder.
  You need to change the fild folder according to the path in your machine, for example, here is another command runs on one mac computer:
    g++ proxy.c -shared -fpic -o libproxy.so -I/System/Library/Frameworks/JavaVM.framework/Versions/A/Headers/ -I/opt/local/include/postgresql91/ -L/opt/local/lib/postgresql91/ -lpq

      3.3.4 java -Djava.library.path=. TxCache
      Also, you could check, change and run the test files, like:
      "javac TestingW.java" to comiple, and then "java -Djava.library.path=. TestingW"
      "javac SnapshotTest.java" to comiple, and then "java -Djava.library.path=. SnapshotTest"
      "javac TestingLoop.java" to comiple, and then "java -Djava.library.path=. TestingLoop"

