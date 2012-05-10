import java.net.ServerSocket;
import java.io.*;
import java.net.*;

public class SocketServer {
	public SocketServer() {
		try {
			int clientcount = 0;
			boolean listening = true;
			ServerSocket server = null; 
			Socket newSocket = null;

			try {
				server = new ServerSocket(2121);
				System.out.println("Server starts...");
			} catch (Exception e) {
				System.out.println("Can not listen to. " + e);
			}

			while (listening) {
				clientcount++;
				newSocket = server.accept();
				new ServerThread(newSocket, clientcount).start();
			}
		} catch (Exception e) {
			System.out.println("Error. " + e);
		}
	}

	public static void main(String[] args) {
		new SocketServer();
	}
}
