#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <jni.h>
#include "JNIFoo.h"

//gcc -shared -fpic -o libfoo.so -I/usr/lib/jvm/jdk1.7.0_03/include/ -I/usr/lib/jvm/jdk1.7.0_03/include/linux/ foo.c

#include <stdio.h>
#include <stdlib.h>
#include "libpq-fe.h"

static void
exit_nicely(PGconn *conn)
{
        PQfinish(conn);
        exit(1);
}

JNIEXPORT jstring JNICALL Java_JNIFoo_nativeFoo (JNIEnv *env, jobject obj)
{
  int i;
  int ds_ret;

  char *newstring;

  jstring ret = 0;

  newstring = (char*)malloc(30);

        const char *conninfo;
        PGconn     *conn;
        PGresult   *res;
        int                     nFields;
        int                     i,
                                j;

  if(newstring == NULL)
  {     
      return ret;
  }

  memset(newstring, 0, 30); 

                conninfo = "dbname=test hostaddr=128.119.247.141 user=keen password=hunter2";

        /* Make a connection to the database */
        conn = PQconnectdb(conninfo);

        /* Check to see that the backend connection was successfully made */
        if (PQstatus(conn) != CONNECTION_OK)
        {
                strcpy(newstring,"connection failed.");
                exit_nicely(conn);
        } else {
		strcpy(newstring,"connection success.");
                exit_nicely(conn);
	}
  

  ret = (*env)->NewStringUTF(env, newstring);

  //free(newstring);

  return ret;
}
