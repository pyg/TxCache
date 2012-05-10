import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.*;
import java.net.*;


public class ServerThread extends Thread {
	private static int number = 0; //client number
	Socket socket = null;  //related Socket object

	public ServerThread(Socket socket, int clientnum) {
		this.socket = socket;
		number = clientnum;
		System.out.println("Online Clients:" + number);
	}

	public void run() {
		try {
			Message message;
			String line;
			ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
			BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
			message = (Message)input.readObject();
			line = message.msg;
			System.out.println("[Client " + number + "]: " + line);
			line = sysin.readLine();
			message = new Message(line);

			while (!line.equals("bye")) {
				output.writeObject(message); 
				output.flush();

				System.out.println("[Server]: " + line);

				message = (Message)input.readObject();
				line = message.msg;
				System.out.println("[Client " + number + "]: " + line);
				line = sysin.readLine();
			}

			output.close();
			input.close();
			socket.close();
		} catch (Exception e) {
			System.out.println("Error. " + e);
		}
	}
}
