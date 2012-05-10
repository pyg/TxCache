import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.*;
import java.net.*;


public class SocketClient {
	public SocketClient() {
		try {
			//send request to local 2121
			Socket socket = new Socket("localhost", 2121);
			System.out.println("Established a connection...");

			ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
			//stdin
			BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
			String line;
			line = sysin.readLine();
			Message message = new Message(line);
			while (!line.equals("bye")) { 
				output.writeObject(message); 
				output.flush();

				System.out.println("[Client]: " + line);
				message = (Message)input.readObject();
				line = message.msg;
				System.out.println("[Server]: " + line);

				line = sysin.readLine();
			}

			output.close();
			input.close();
			socket.close();
		} catch (Exception e) {
			System.out.println("Error. " + e);
		}
	}

	public static void main(String[] args) {
		new SocketClient();
	}
}
