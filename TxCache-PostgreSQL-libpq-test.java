/**
 * A demo program to show how jdbc works with postgresql
 * Nick Fankhauser 10/25/01
 * nickf@ontko.com or nick@fankhausers.com
 * This program may be freely copied and modified
 * Please keep this header intact on unmodified versions
 * The rest of the documentation that came with this demo program
 * may be found at http://www.fankhausers.com/postgresql/jdbc
 */



import java.sql.*;   // All we need for JDBC
import org.sourceforge.jxdbcon.postgresql.*;
import org.sourceforge.jxdbcon.postgresql.LibPQ;
import org.sourceforge.jxutil.*;
import org.sourceforge.jxutil.SCCI;

public class TestDB
{
	static Connection       db;        // A connection to the database
	static Statement        sql;       // Our statement to run queries with
	static DatabaseMetaData dbmd;      // This is basically info the driver delivers
	// about the DB it just connected to. I use
	// it to get the DB version to confirm the
	// connection in this example.
	
	public static void test(String argv[])
		throws ClassNotFoundException, SQLException
	{
		String hostname = argv[0];
		String database = argv[1];
		String username = argv[2];
		String password = argv[3];
	    /*
		Class.forName("org.postgresql.Driver"); //load the driver
		db = DriverManager.getConnection("jdbc:postgresql://"+hostname+":5432/"+database,
		        username,
		        password); //connect to the db
		*/
		
		Class.forName("org.sourceforge.jxdbcon.JXDBConDriver"); //load the driver
		/*
		 db = DriverManager.getConnection("jdbc:postgresql:net//"+hostname+":5432/"+database,
		 
		                                 username,
		                                 password); //connect to the db
		*/
		int pgconn = LibPQ.PQconnectdb("dbname=test hostaddr=emotion.cs.umass.edu user=keen password=hunter2");
		System.out.println("connected: " + pgconn);
		System.out.println("db: " + LibPQ.PQdb(pgconn));
		System.out.println("user: " + LibPQ.PQuser(pgconn));
		System.out.println("error: " + LibPQ.PQerrorMessage(pgconn));
		                               
		System.out.println("Connection to DB seems successful.\n");
	
	/*
	dbmd = db.getMetaData(); //get MetaData to confirm connection
	System.out.println("Connection to "+dbmd.getDatabaseProductName()+" "+
	   dbmd.getDatabaseProductVersion()+" successful.\n");
	sql = db.createStatement(); //create a statement that we can use later
	
	String sqlText = "drop table jdbc_demo";
	System.out.println("Executing this command: "+sqlText+"\n");
	try {
	  sql.executeUpdate(sqlText);
	}
	catch (SQLException ex) {
	  if (!ex.toString().equals("org.postgresql.util.PSQLException: ERROR: table \"jdbc_demo\" does not exist"))
	    throw ex;
	}
	
	
	sqlText = "create table jdbc_demo (code int, text varchar(20))";
	System.out.println("Executing this command: "+sqlText+"\n");
	    sql.executeUpdate(sqlText);
	
	 
	    sqlText = "insert into jdbc_demo values (1,'One');insert into jdbc_demo values (2,'Two');update jdbc_demo set text = 'Only' where code = 1";
	System.out.println("Executing this command: "+sqlText+"\n");
	sql.executeUpdate(sqlText);
	
	
	//    sqlText = "update jdbc_demo set text = 'Only' where code = 1";
	//    System.out.println("Executing this command: "+sqlText+"\n");
	//    sql.executeUpdate(sqlText);
	System.out.println (sql.getUpdateCount()+
	                    " rows were update by this statement\n");
	
	sqlText = "BEGIN READ ONLY ISOLATION LEVEL SERIALIZABLE;PIN;";
	sql.executeUpdate(sqlText);
	sqlText = "SELECT * FROM jdbc_demo WHERE code=1";
	//System.out.println("Now executing the command: "+
	//                   "select * from jdbc_demo");
	//ResultSet results = sql.executeQuery("select * from jdbc_demo");
	ResultSet results = sql.executeQuery(sqlText);
	//results.getMetaData();
	if (results != null)
	{
	  while (results.next())
	  {
	    System.out.println("code = "+results.getInt("code")+
	   "; text = "+results.getString(2)+"\n");
	      }
	    }
	    results.close();
	
	    db.close();
	    */
	  }
	
	  public static void correctUsage()
	  {
	    System.out.println("\nIncorrect number of arguments.\nUsage:\n "+
	   "java hostname database username password\n");
	    System.exit(1);
	  }
	
	  public static void main (String args[])
	  {
	    if (args.length != 4) correctUsage();
	    try
	    {
	      test(args);
	    }
	    catch (Exception ex)
	    {
	      System.out.println("***Exception:\n"+ex);
	      ex.printStackTrace();
	    }
	  }
}