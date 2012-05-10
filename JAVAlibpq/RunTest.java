public class RunTest {
	static String conninfo = "dbname=test host=emotion.cs.umass.edu user=keen password=hunter2";
	
	public static void main (String[] args) {
	ConnectDB db = new ConnectDB();
String init = db.UMASSPQinitialize();

	
	
		Testing a = new Testing(db);
		new Thread(a).start();
		
		Testing b = new Testing(db);
		new Thread(b).start();
		/*
		Testing c = new Testing(db);
		new Thread(c).start();
		Testing d = new Testing(db);
		new Thread(d).start();
		*/
	}
}