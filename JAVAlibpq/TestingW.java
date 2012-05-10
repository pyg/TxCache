//package testApp;

//javac -classpath . TestingW.java
//java -Djava.library.path=. -classpath . Testing




import java.util.Date;

public class TestingW {
	static String conninfo = "dbname=test host=emotion.cs.umass.edu user=keen password=hunter2";
    //ConnectDB db;
    //String init;
    //String conn;
    
    public static long executeq(String command, String conn) throws Exception{
        long time = (new Date()).getTime();
		
        String res = TxCache.TxPQexec(conn, command);
        
		for (int i = 0; i < TxCache.PQntuples(res); ++i) {
			for (int j = 0; j < TxCache.PQnfields(res); ++j) {
				//System.out.print(String.format("%s\t",TxCache.wrap("TxCache","PQgetvalue",res,i,j)));
                System.out.print(String.format("%s\t",TxCache.PQgetvalue(res,i,j)));
			}
			System.out.println();
		}
        TxCache.PQclear(res);
        System.out.println((new Date()).getTime() - time);
        return (new Date()).getTime() - time;
	}
	
	public static void main(String[] args) {
String conninfo = "dbname=test host=emotion.cs.umass.edu user=keen password=hunter2";
        
        TxCache.initializeTxCache();
        String conn = TxCache.TxPQconnectdb(conninfo,true);
        //TxCache.wrap("TestingW","executeq","select * from testuser limit 100;",conn);
        
        
		/*
ConnectDB db = new ConnectDB();
String init = db.UMASSPQinitialize();
String conn = db.PQconnectdb(conninfo);
        */
        //String conn = db.PQconnectdb(conninfo);
//TestingW.executeu("insert into userpref (name,preference,color,fid) values ('test1','testpreference',5,233456;",conn);
        try {
            TxCache.wrap("TestingW","executeq",conn,"select * from testuser limit 100;",conn);
            TxCache.wrap("TestingW","executeq",conn,"select * from testuser limit 100;",conn);
            TxCache.wrap("TestingW","executeq",conn,"select * from testuser limit 100;",conn);
        } catch (Exception e) {}
            /*
TestingW.executeq("select * from userpref,testuser where userpref.color = 7 and userpref.name = testuser.name limit 10;",conn);
TestingW.executeq("select * from userpref where fid > 984653 and fid < 984853;",conn);
TestingW.executeq("select * from testuser where id = 984653;",conn);
TestingW.executeq("select * from testuser where name = 0.278535296209157;",conn);
TestingW.executeq("select * from userpref where preference like '%738587%' 100;",conn);
TestingW.executeq("select * from userpref where preference like '%7385877%' 100;",conn);
        System.out.println();
        TestingW.executeq("select * from testuser limit 100;",conn);
        TestingW.executeq("select * from userpref,testuser where userpref.color = 7 and userpref.name = testuser.name limit 10;",conn);
        TestingW.executeq("select * from userpref where fid > 984653 and fid < 984853;",conn);
        TestingW.executeq("select * from testuser where id = 984653;",conn);
        TestingW.executeq("select * from testuser where name = 0.278535296209157;",conn);
        TestingW.executeq("select * from userpref where preference like '%738587%' 100;",conn);
        TestingW.executeq("select * from userpref where preference like '%7385877%' 100;",conn);
        System.out.println();
        TestingW.executeq("select * from testuser limit 100;",conn);
        TestingW.executeq("select * from userpref,testuser where userpref.color = 7 and userpref.name = testuser.name limit 10;",conn);
        TestingW.executeq("select * from userpref where fid > 984653 and fid < 984853;",conn);
        TestingW.executeq("select * from testuser where id = 984653;",conn);
        TestingW.executeq("select * from testuser where name = 0.278535296209157;",conn);
        TestingW.executeq("select * from userpref where preference like '%738587%' 100;",conn);
        TestingW.executeq("select * from userpref where preference like '%7385877%' 100;",conn);
        System.out.println();
        TxCache.TxPQfinish(conn);
        } catch (Exception e) { System.out.println(e);}
//TestingW.executeu("insert into userpref (name,preference,color,fid) values ('test1','testpreference',5,233456;");

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
