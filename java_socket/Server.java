import java.io.*;
import java.net.*;

public class Server {
	private static int port_num = 3435;
	public static void main(String[] arg) {
		ServerSocket conn;
		Socket sv_socket;
		ObjectInputStream input;
		ObjectOutputStream output;
		try {
			Employee person;
			BufferedReader read=new BufferedReader(new InputStreamReader(System.in)); 
			while(true) {
				conn = new ServerSocket(port_num);	
				sv_socket = conn.accept();
				input = new ObjectInputStream(sv_socket.getInputStream());
				output = new ObjectOutputStream(sv_socket.getOutputStream());
				System.out.println("input:");		
				String s=read.readLine();
				if(s.equals("quit")) {
					break;
				} else {
					person = (Employee)input.readObject();
					person.heard();
					person.message = s;
					person.said();
					output.writeObject(person);
					output.flush();
				}
				conn.close();
				sv_socket.close();
				input.close();		
				output.close();
			}
				

		} catch (Exception e) {
			System.out.println("error:"+e); 

		} 
	}
}
