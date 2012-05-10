import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.*;
import java.util.HashMap;
import java.sql.*;
import java.util.*;

public class WebApp {
	private WebApp() { }
	public static HashMap<String, Object> cache;
	
	public static void main(String args[]) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException, IOException {
		try
		{
            
			
			//Class.forName("org.postgresql.Driver");
            //			Class.forName("org.sourceforge.jxdbcon.JXDBConDriver");
			
			String dbURL = "jdbc:postgresql://emotion.cs.umass.edu/test";
			
			Connection dbCon = DriverManager.getConnection(dbURL, "keen", "hunter2");
			
			
			
			Statement st = dbCon.createStatement();
			ResultSet rs = st.executeQuery("SELECT VERSION()");
			
			if (rs.next()) {
				System.out.println(rs.getString(1));
			}
		} catch (SQLException ex) {
			System.out.println("fail");
		}
		WebApp.cache = new HashMap<String, Object>();
		
		//double x = (Double) call("WebApp","add",new Object[] {(double)1000000000.0, (double)2});
		double x = (Double) call("WebApp","add",(double)1000000000.0, (double)2);
		System.out.println(x);
		//double y = (Double) call("WebApp","add",new Object[] {(double)1000000000.0, (double)2});
		double y = (Double) call("WebApp","add",(double)1000000000.0, (double)2);
		System.out.println(y);
		TheClass cl = new TheClass();
		cl.x=10;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(cl);
		String string2 = baos.toString();
		TheClass cl1 = new TheClass();
		cl1.x=10;
		baos = new ByteArrayOutputStream();
		oos = new ObjectOutputStream(baos);
		oos.writeObject(cl1);
		String string1 = baos.toString();
		System.out.println(string1);
		System.out.println(string2);
		System.out.println(string1.equals(string2));
		System.out.println(cl.toString());
		//double x1 = (Double) call("WebApp","minus",new Object[] {cl, (double)2});
		double x1 = (Double) call("WebApp","minus",cl, (double)2);
		System.out.println(x1);
		//double y1 = (Double) call("WebApp","minus",new Object[] {cl1, (double)2});
		double y1 = (Double) call("WebApp","minus",cl1, (double)2);
		System.out.println(y1);
		System.out.println(cache);
	}
    
	public static Object call (String clazz, String method, Object... args) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException {
		//String clazz = 
		Class<?> theClass = Class.forName(clazz);
		Class<?>[] arguments = new Class<?>[args.length];
		
		String key = clazz + ".." + method;
		for (int i = 0; i < args.length; ++i) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(args[i]);
			key += ".." + baos.toString();
			//key += ".." + args[i].toString();
			arguments[i] = args[i].getClass();
		}
		
		if(cache.containsKey(key)) {
			return cache.get(key);
		}
		
		Method toCall = theClass.getMethod(method, arguments);
		
		Object result;
		if (theClass.getName() == "Statement" && toCall.getName() == "executeQuery") {
			result = toCall.invoke(TxCache.class, args); // Connection create statement, resultset?
		} else {
			result = toCall.invoke(theClass, args);
		}
		
		cache.put(key, result);
		
		return result;
		
	}
	
	public static double add (Double a,Double b) {
		int j = 0;
		while(j < a) j++;
		return a+b;
	}
	
	public static double minus (TheClass a,Double b) {
		int j = 0;
		while(j < 1000000000) j++;
		return 1+b;
	}
	
    
    
}