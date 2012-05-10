import java.io.*;
import java.net.*;

public class Server {
	private static int port_num = 3435;
	public static void main(String[] arg) {
		ServerSocket conn;
		Socket sv_socket;
		ObjectInputStream input;
		ObjectOutputStream output;
		
		Employee person;
		BufferedReader read=new BufferedReader(new InputStreamReader(System.in)); 

		try {
		conn = new ServerSocket(port_num);	
		sv_socket = conn.accept();
		input = new ObjectInputStream(sv_socket.getInputStream());
		output = new ObjectOutputStream(sv_socket.getOutputStream());

		while(true) {
			try {
				System.out.println("input:");		
				String s=read.readLine();
				if(s.equals("quit")) {
					conn.close();
					sv_socket.close();
					input.close();		
					output.close();
					break;
				} else {
					try {
						person = (Employee)input.readObject();
					} catch(NullPointerException e) {
						System.out.println("error:"+e); 
						conn.close();
						sv_socket.close();
						input.close();
						output.close();
						conn = new ServerSocket(port_num);	
						sv_socket = conn.accept();
						input = new ObjectInputStream(sv_socket.getInputStream());
						output = new ObjectOutputStream(sv_socket.getOutputStream());
						continue;
					}
					person.heard();
					person.message = s;
					person.said();
					output.writeObject(person);
					output.flush();
				}
			} catch (Exception e) {
				System.out.println("error:"+e); 
				conn.close();
				sv_socket.close();
				input.close();
				output.close();
				conn = new ServerSocket(port_num);	
				sv_socket = conn.accept();
				input = new ObjectInputStream(sv_socket.getInputStream());
				output = new ObjectOutputStream(sv_socket.getOutputStream());
			} 
		}
		} catch(Exception e) {
			System.out.println("error in beginning:"+e);
		}
	}
}
