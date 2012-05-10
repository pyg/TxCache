import java.io.*;
import java.net.*;

public class Client {
	private static int port_num = 3435;

	public static void main(String[] arg) {
		Socket conn; 
		ObjectOutputStream out; 
		ObjectInputStream input;
		try {
			BufferedReader read=new BufferedReader(new InputStreamReader(System.in)); 
			Employee katie = new Employee(12, "katie");
			while(true) {
				conn = new Socket("localhost", port_num); 
				out = new ObjectOutputStream(conn.getOutputStream()); 
				input = new ObjectInputStream(conn.getInputStream());

				System.out.println("input:"); 
				String s=read.readLine();
				if(s.equals("quit")) {
					out.close(); 
					input.close();
					conn.close();
					break;
				}				
				katie.message = s;
				out.writeObject(katie); 
				out.flush();
				katie= (Employee)input.readObject(); 
				katie.heard();
				conn.setReuseAddress(true);
				out.close(); 
				input.close();
				conn.close();
			}
//			conn.setReuseAddress(true);
		} catch (Exception e) {
			System.out.println("error:"+e); 
		} 
	} 
}
