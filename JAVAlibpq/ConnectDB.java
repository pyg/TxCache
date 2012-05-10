//javac JNIFoo.java
//javah -jni JNIFoo

//java -Djava.library.path=. ConnectDB

public class ConnectDB {
	final int CONNECTION_OK = 0;
	final int PGRES_COMMAND_OK = 1;
	final String INITIALIZATION_SUCCESS = "SUCCESS";

	/*
	class PGconn {
		public String conn;
	}
	class PGstatus {
		public int status;
	}
	class PGresult {
		public String result;
	}
	class PGresultStatus {
		public int resultStatus;
	}
    class PGcmdStatus {
		public String cmdStatus;
	}
	*/

	public native String UMASSPQinitialize();
	
    public native String PQconnectdb(String conninfo);
    public native int PQstatus(String conn);
	public native String PQerrorMessage(String conn);
	public native String PQexec(String conn, String sql);
	public native int PQresultStatus(String res);
	public native void PQclear(String res);
	public native String PQcmdStatus(String res);
	public native void PQfinish(String conn);
	public native int PQntuples(String res);
	public native int PQnfields(String res);
	public native String PQgetvalue(String res, int i, int j);


    static {
        System.loadLibrary("proxy");
    }
    
    private void exit_nicely(String conn) {
    	PQfinish(conn);
		System.exit(1);
	}

	public void print (String[] args) {
		String init = UMASSPQinitialize();
		if (!init.equals(INITIALIZATION_SUCCESS)) {
			System.err.println("DB Initialization Error");
			return;
		}
		else System.out.println("Initialized: " + init);
		/*
		 * If the user supplies a parameter on the command line, use it as
		 * the conninfo string; otherwise default to setting dbname=template1
		 * and using environment variables or defaults for all other connection
		 * parameters.
		 */
		String conninfo;		
		if (args.length > 1)
			conninfo = args[0];
		else
			conninfo = "dbname=test host=emotion.cs.umass.edu user=keen password=hunter2";

		/* Make a connection to the database */
		String conn = PQconnectdb(conninfo);

		/* Check to see that the backend connection was successfully made */
		
		if (PQstatus(conn) != CONNECTION_OK)
		{
		    System.err.println("Connection to database failed: " + PQerrorMessage(conn));
		    exit_nicely(conn);
		}

		/*
		 * Our test case here involves using a cursor, for which we must be
		 * inside a transaction block.  We could do the whole thing with a
		 * single PQexec() of "select * from pg_database", but that's too
		 * trivial to make a good example.
		 */

		/* Start a transaction block */
		//res = PQexec(conn, "BEGIN");
		String res = PQexec(conn, "BEGIN READ ONLY ISOLATION LEVEL SERIALIZABLE;PIN;");
		   
		if (PQresultStatus(res) != PGRES_COMMAND_OK)
		{
			System.err.println("BEGIN command failed: " + PQerrorMessage(conn));
			PQclear(res);
			exit_nicely(conn);
		}
		/*
		 * Should PQclear PGresult whenever it is no longer needed to avoid
		 * memory leaks
		 */
		PQclear(res);

		/*
		 * Fetch rows from pg_database, the system catalog of databases
		 */
		//res = PQexec(conn, "DECLARE myportal CURSOR FOR select * from pg_database");

		res = PQexec(conn, "SELECT * from foo;");
		/*
		if (PQresultStatus(res) != PGRES_COMMAND_OK) {
			fprintf(stderr, "SELECT failed: %s", PQerrorMessage(conn));
			PQclear(res);
			exit_nicely(conn);
		}
		PQclear(res);*/
		
		/*
		res = PQexec(conn, "FETCH ALL in myportal");
		if (PQresultStatus(res) != PGRES_TUPLES_OK) {
			fprintf(stderr, "FETCH ALL failed: %s", PQerrorMessage(conn));
			PQclear(res);
			exit_nicely(conn);
		}
	
		// first, print out the attribute names 
		nFields = PQnfields(res);
		for (i = 0; i < nFields; i++)
			printf("%-15s", PQfname(res, i));
		printf("\n\n");

		// next, print out the rows
		for (i = 0; i < PQntuples(res); i++) {
			for (j = 0; j < nFields; j++)
				printf("%-15s", PQgetvalue(res, i, j));
			printf("\n");
		}
		*/
		System.out.println("cmdStatus: " + PQcmdStatus(res));
		System.out.println("resultStatus: " + PQresultStatus(res));
		PQclear(res);

		// close the portal ... we don't bother to check for errors ...
	/*	res = PQexec(conn, "CLOSE myportal");
		PQclear(res);*/

		/* end the transaction */
		res = PQexec(conn, "COMMIT;");
		PQclear(res);
		/* close the connection to the database and cleanup */
		PQfinish(conn);
    }

	public static void main(String[] args) {
    	//(new ConnectDB()).print(args);
    	ConnectDB db = new ConnectDB();
		String init = db.UMASSPQinitialize();
		String conninfo = "dbname=test host=emotion.cs.umass.edu user=keen password=hunter2";
		String conn = db.PQconnectdb(conninfo);

		String res = db.PQexec(conn, "select * from testuser limit 100;");
		for (int i = 0; i < db.PQntuples(res); ++i) {
			for (int j = 0; j < db.PQnfields(res); ++j) {
				System.out.print(String.format("%s\t",db.PQgetvalue(res,i,j)));
			}
			System.out.println();
		}
    	return;
    }
}
