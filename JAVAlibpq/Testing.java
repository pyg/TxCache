//package testApp;

//javac -classpath . Testing.java
//java -Djava.library.path=. -classpath . Testing




import java.util.Date;

public class Testing implements Runnable {
	static String conninfo = "dbname=test host=emotion.cs.umass.edu user=keen password=hunter2";
    ConnectDB db;
    //String init;
    //String conn;
    
    public Testing(ConnectDB db) {
        this.db = db;
       // this.conn = conn;
    }
	public static long executeq(String command, ConnectDB db, String conn) {
        long time = (new Date()).getTime();
		
		String res = db.PQexec(conn, command);
        /*
		for (int i = 0; i < db.PQntuples(res); ++i) {
			for (int j = 0; j < db.PQnfields(res); ++j) {
				//System.out.print(String.format("%s\t",db.PQgetvalue(res,i,j)));
			}
			//System.out.println();
		}
         */
		db.PQclear(res);
        System.out.println((new Date()).getTime() - time);
        return (new Date()).getTime() - time;
	}
	
	public static void executeu(String command, ConnectDB db, String conn) {
		//String conn = db.PQconnectdb(conninfo);
		String res = db.PQexec(conn, command);
		db.PQclear(res);
	}
	
	public void run() {

		/*
ConnectDB db = new ConnectDB();
String init = db.UMASSPQinitialize();
String conn = db.PQconnectdb(conninfo);
        */
        String conn = db.PQconnectdb(conninfo);
Testing.executeu("insert into userpref (name,preference,color,fid) values ('test1','testpreference',5,233456;",db,conn);
Testing.executeq("select * from testuser limit 100;",db,conn);
Testing.executeq("select * from userpref,testuser where userpref.color = 7 and userpref.name = testuser.name limit 10;",db,conn);
Testing.executeq("select * from userpref where fid > 984653 and fid < 984853;",db,conn);
Testing.executeq("select * from testuser where id = 984653;",db,conn);
Testing.executeq("select * from testuser where name = 0.278535296209157;",db,conn);
Testing.executeq("select * from userpref where preference like '%738587%' 100;",db,conn);
Testing.executeq("select * from userpref where preference like '%7385877%' 100;",db,conn);
        System.out.println();
        Testing.executeq("select * from testuser limit 100;",db,conn);
        Testing.executeq("select * from userpref,testuser where userpref.color = 7 and userpref.name = testuser.name limit 10;",db,conn);
        Testing.executeq("select * from userpref where fid > 984653 and fid < 984853;",db,conn);
        Testing.executeq("select * from testuser where id = 984653;",db,conn);
        Testing.executeq("select * from testuser where name = 0.278535296209157;",db,conn);
        Testing.executeq("select * from userpref where preference like '%738587%' 100;",db,conn);
        Testing.executeq("select * from userpref where preference like '%7385877%' 100;",db,conn);
        System.out.println();
        Testing.executeq("select * from testuser limit 100;",db,conn);
        Testing.executeq("select * from userpref,testuser where userpref.color = 7 and userpref.name = testuser.name limit 10;",db,conn);
        Testing.executeq("select * from userpref where fid > 984653 and fid < 984853;",db,conn);
        Testing.executeq("select * from testuser where id = 984653;",db,conn);
        Testing.executeq("select * from testuser where name = 0.278535296209157;",db,conn);
        Testing.executeq("select * from userpref where preference like '%738587%' 100;",db,conn);
        Testing.executeq("select * from userpref where preference like '%7385877%' 100;",db,conn);
        System.out.println();
        db.PQfinish(conn);

//Testing.executeu("insert into userpref (name,preference,color,fid) values ('test1','testpreference',5,233456;");

/*


String res = db.PQexec(conn, "select * from testuser limit 100;");
for (int i = 0; i < db.PQntuples(res); ++i) {
	for (int j = 0; j < db.PQnfields(res); ++j) {
		System.out.print(String.format("%-15s",db.PQgetvalue(res,i,j)));
	}
	System.out.println();
}

res = db.PQexec(conn, "select * from userpref,testuser where userpref.color = 7 and userpref.name = testuser.name limit 10;");
for (int i = 0; i < db.PQntuples(res); ++i) {
	for (int j = 0; j < db.PQnfields(res); ++j) {
		System.out.print(String.format("%-15s",db.PQgetvalue(res,i,j)));
	}
	System.out.println();
}

res = db.PQexec(conn, "select * from userpref where fid > 984653 and fid < 984853");
for (int i = 0; i < db.PQntuples(res); ++i) {
	for (int j = 0; j < db.PQnfields(res); ++j) {
		System.out.print(String.format("%-15s",db.PQgetvalue(res,i,j)));
	}
	System.out.println();
}

res = db.PQexec(conn, "select * from testuser where id = 984653;");
for (int i = 0; i < db.PQntuples(res); ++i) {
	for (int j = 0; j < db.PQnfields(res); ++j) {
		System.out.print(String.format("%-15s",db.PQgetvalue(res,i,j)));
	}
	System.out.println();
}

res = db.PQexec(conn, "select * from testuser where name = 0.278535296209157;");
for (int i = 0; i < db.PQntuples(res); ++i) {
	for (int j = 0; j < db.PQnfields(res); ++j) {
		System.out.print(String.format("%-15s",db.PQgetvalue(res,i,j)));
	}
	System.out.println();
}

res = db.PQexec(conn, "select * from userpref where preference like '%738587%' 100;");
for (int i = 0; i < db.PQntuples(res); ++i) {
	for (int j = 0; j < db.PQnfields(res); ++j) {
		System.out.print(String.format("%-15s",db.PQgetvalue(res,i,j)));
	}
	System.out.println();
}

res = db.PQexec(conn, "select * from userpref where preference like '%7385877%' 100;");
for (int i = 0; i < db.PQntuples(res); ++i) {
	for (int j = 0; j < db.PQnfields(res); ++j) {
		System.out.print(String.format("%-15s",db.PQgetvalue(res,i,j)));
	}
	System.out.println();
}


out.println("cmdStatus: " + db.PQcmdStatus(res));
out.println("resultStatus: " + db.PQresultStatus(res));
*/

	}
}
