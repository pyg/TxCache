/*
 * testlibpq.c
 *
 *              Test the C version of LIBPQ, the POSTGRES frontend library.
 */
#include <stdio.h>
#include <stdlib.h>
#include "libpq-fe.h"

static void
exit_nicely(PGconn *conn)
{
        PQfinish(conn);
        exit(1);
}

int
main(int argc, char **argv)
{
        const char *conninfo;
        PGconn     *conn;
        PGresult   *res;
        int                     nFields;
        int                     i,
                                j;

        if (argc > 1)
                conninfo = argv[1];
        else
                conninfo = "dbname=test hostaddr=128.119.247.141 user=keen password=hunter2";

        /* Make a connection to the database */
        conn = PQconnectdb(conninfo);

        /* Check to see that the backend connection was successfully made */
        if (PQstatus(conn) != CONNECTION_OK)
        {
                fprintf(stderr, "Connection to database failed: %s",
                        PQerrorMessage(conn));
                exit_nicely(conn);
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

        return 0;
}
