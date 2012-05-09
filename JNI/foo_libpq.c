#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <jni.h>
#include "JNIFoo.h"

//gcc -shared -fpic -o libfoo.so -I/usr/lib/jvm/jdk1.7.0_03/include/ -I/usr/lib/jvm/jdk1.7.0_03/include/linux/ foo.c

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

	//***begin:in the beginning of the whole program, make it listen
	res=PQexec(conn,"\set QUIET off");
	res=PQexec(conn,"LISTEN invalidation;");
	printf("%-25s\n", PQcmdStatus(res));
	//***end:make it listen ends

        res=PQexec(conn, "BEGIN ISOLATION LEVEL SERIALIZABLE;INSERT INTO bar VALUES (10);");
	//***begin:invalidation part, from commit
	res = PQexec(conn,"COMMIT;");
	PGnotify   *notify;
	const char invalidation_tag[25] = {"invalidation"};
        while ((notify = PQnotifies(conn)) != NULL) {
		if(strcmp(invalidation_tag,notify->relname) == 0) {
			PGresult   *res_tags;
			res_tags = PQexec(conn, "SELECT * from pg_invalidations;");
        		//print out the rows 
       			for (i = 0; i < PQntuples(res_tags); i++) {
        	                printf("%d\t", atoi(PQgetvalue(res_tags, i, 0)));
				printf("%s-15", PQgetvalue(res_tags, i, 1));
        	        	printf("\n");
        		}
			PQclear(res_tags);
		}
		PQfreemem(notify);
        }
	//***end:invalidation part

        PQclear(res);
        /* close the portal ... we don't bother to check for errors ... */
        res = PQexec(conn, "CLOSE myportal");
        PQclear(res);

        /* end the transaction */
        res = PQexec(conn, "END");
        PQclear(res);
		
        /* close the connection to the database and cleanup */
        PQfinish(conn);
  
  //strcpy(newstring,"hello");
  ret = (*env)->NewStringUTF(env, newstring);

  //free(newstring);

  return ret;
}
