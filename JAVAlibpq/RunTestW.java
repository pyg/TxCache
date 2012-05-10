public class RunTestW {
	static String conninfo = "dbname=test host=emotion.cs.umass.edu user=keen password=hunter2";
	
	public static void main (String[] args) {	
	
		TestingW a = new TestingW();
		new Thread(a).start();
		
        /*
		TestingW b = new TestingW();
		new Thread(b).start();
		/*
		Testing c = new Testing(db);
		new Thread(c).start();
		Testing d = new Testing(db);
		new Thread(d).start();
		*/
	}
}