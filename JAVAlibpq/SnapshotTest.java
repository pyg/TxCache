public SnapshotTest {

	public static long executeq(String command, String conn) throws Exception{
        String res = TxCache.TxPQexec(conn, command);
        
		for (int i = 0; i < TxCache.PQntuples(res); ++i) {
			for (int j = 0; j < TxCache.PQnfields(res); ++j) {
				//System.out.print(String.format("%s\t",TxCache.wrap("TxCache","PQgetvalue",res,i,j)));
                		System.out.print(String.format("%s\t",TxCache.PQgetvalue(res,i,j)));
			}
			System.out.println();
		}
        TxCache.PQclear(res);
        return (new Date()).getTime() - time;
    }
	public static void main (String[] args) {
		TxCache.initializeTxCache();
		String conninfo = "dbname=test host=emotion.cs.umass.edu user=keen password=hunter2";
		String conn = TxCache.TxPQconnectdb(conninfo,true);
		
		TxCache.BEGIN_RO(conn,2);
		long res = TxCache.wrap("SnapshotTest","executeq","","select name from testuser where id = 50;");
		System.out.println(res);
		TxCache.COMMIT(conn);
		
		TxCache.BEGIN_RW(conn);
		res = TxCache.wrap("SnapshotTest","executeq","","update testuser set name = random()::text where id = 50;");
		TxCache.COMMIT(conn);
		System.out.println(res);
		
		TxCache.BEGIN_RO(conn,2);
		long res = TxCache.wrap("SnapshotTest","executeq","","select name from testuser where id = 50;");
		System.out.println(res);
		TxCache.COMMIT(conn);
		
		TxCache.BEGIN_RO(conn,2);
		long res = TxCache.wrap("SnapshotTest","executeq","","select name from testuser where id = 50;");
		System.out.println(res);
		TxCache.COMMIT(conn);
		
	}
}