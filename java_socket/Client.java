import java.io.*;
import java.net.*;

public class Client {
	private static int port_num = 3435;

	public static void main(String[] arg) {
		Socket conn; 
		ObjectOutputStream out; 
		ObjectInputStream input;
		Employee katie = new Employee(12, "katie");
		try {
			BufferedReader read=new BufferedReader(new InputStreamReader(System.in)); 

			conn = new Socket("localhost", port_num); 
			out = new ObjectOutputStream(conn.getOutputStream()); 
			input = new ObjectInputStream(conn.getInputStream());
			while(true) {
				try {
					System.out.println("input:"); 
					String s=read.readLine();
					if(s.equals("quit")) {
						out.writeObject(null); 
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
				} catch (Exception e) {
					System.out.println("catched in client");
					conn.setReuseAddress(true);
					out.close(); 
					input.close();
					conn.close();
					conn = new Socket("localhost", port_num); 
					out = new ObjectOutputStream(conn.getOutputStream()); 
					input = new ObjectInputStream(conn.getInputStream());
				}
			}
//			conn.setReuseAddress(true);
		} catch (Exception e) {
			System.out.println("error:"+e); 
		} 
	} 
}
