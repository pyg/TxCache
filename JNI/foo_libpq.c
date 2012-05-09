#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <jni.h>
#include "JNIFoo.h"

//gcc -shared -fpic -o libfoo.so -I/usr/lib/jvm/java-6-openjdk/include/ -I/usr/lib/jvm/java-6-openjdk/include/linux/ -I/usr/include/postgresql/ -L/usr/include/postgresql/ -lpq foo_libpq.c

#include "libpq-fe.h"

static void
exit_nicely(PGconn *conn)
{
        PQfinish(conn);
        exit(1);
}

JNIEXPORT jstring JNICALL Java_JNIFoo_nativeFoo (JNIEnv *env, jobject obj)
{
  int ds_ret;

  char *newstring;

  jstring ret = 0;

  newstring = (char*)malloc(30);

        char conninfo[100];
        PGconn     *conn;
        PGresult   *res;
        int                     nFields;
        int                     i,
                                j;
	memset(newstring, 0, 30); 

        strcpy(conninfo,"dbname=test hostaddr=128.119.247.141 user=keen password=hunter2");

        //Make a connection to the database
        conn = PQconnectdb(conninfo);

        //Check to see that the backend connection was successfully made 
        if (PQstatus(conn) != CONNECTION_OK)
        {
                strcpy(newstring,"connection failed.");
                exit_nicely(conn);
        } else {
		strcpy(newstring,"connection success.");
                
	}
  
  //strcpy(newstring,"hello");
  ret = (*env)->NewStringUTF(env, newstring);

  //free(newstring);

  return ret;
}
