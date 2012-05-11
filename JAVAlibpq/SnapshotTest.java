import java.util.Date;

public class SnapshotTest {

	public static String executeq(String command, String conn) throws Exception{
        String res = TxCache.TxPQexec(conn, command);
        
        String result = "";
		for (int i = 0; i < TxCache.PQntuples(res); ++i) {
			for (int j = 0; j < TxCache.PQnfields(res); ++j) {
				//System.out.print(String.format("%s\t",TxCache.wrap("TxCache","PQgetvalue",res,i,j)));
                		result += String.format("%s\t",TxCache.PQgetvalue(res,i,j));
			}
			result += "\n";
		}
        TxCache.PQclear(res);
        return result;
    }
	public static void main (String[] args) {
		TxCache.initializeTxCache();
		
		try {
			String conninfo = "dbname=test host=emotion.cs.umass.edu user=keen password=hunter2";
			String conn = TxCache.TxPQconnectdb(conninfo,true);
		
			TxCache.BEGIN_RO(conn,2);
			long res = TxCache.wrap("SnapshotTest","executeq",conn,"select d from baz where d = 3;",conn);
			System.out.println(res);
			TxCache.COMMIT(conn);
		
			TxCache.BEGIN_RW(conn);
			res = TxCache.wrap("SnapshotTest","executeq",conn,"update baz set e = random()::text where d = 3;",conn);
			TxCache.COMMIT(conn);
			System.out.println(res);
		
			TxCache.BEGIN_RO(conn,0);
			res = TxCache.wrap("SnapshotTest","executeq",conn,"select d from baz where d = 3;",conn);
			System.out.println(res);
			TxCache.COMMIT(conn);
			
			TxCache.BEGIN_RO(conn,0);
			res = TxCache.wrap("SnapshotTest","executeq",conn,"select d from baz where d = 3;",conn);
			System.out.println(res);
			TxCache.COMMIT(conn);
		
			TxCache.BEGIN_RW(conn);
			res = TxCache.wrap("SnapshotTest","executeq",conn,"update testuser set name = random()::text where id = 50;",conn);
			TxCache.COMMIT(conn);
			System.out.println(res);
		
			TxCache.BEGIN_RO(conn,2);
			res = TxCache.wrap("SnapshotTest","executeq",conn,"select name from testuser where id = 50;",conn);
			System.out.println(res);
			TxCache.COMMIT(conn);
			
		} 
		catch (Exception ex) {			
			ex.printStackTrace();
		}
	}
}
