package http;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
/**
 * HTTPConnection represents a single client connection to this server.
 *
 * @version 1.0
 */
public class HTTPConnection implements Runnable {
	//Already connected socket for one client.
	private Socket client;

	//Input channel.
	private Scanner socketInput;

	//Current state of this connection. If EOF or any errors are detected, set this to false.
	private boolean connected;

	// Required constructor.@param client client's socket created in the main thread
	HTTPConnection(Socket client) {
		this.client = client;
	}

	//Handle a single request message.
	private void processRequest() {
		try{
			var http = new HTTPRequest(socketInput);
			if (http.isValid()) {
				System.out.println(client+ ": Recieved valid request for " + http.getHostHeader() + http.getPath());
			}
			else {
				System.out.println(client + ": Recieved invalid request");
				connected =false;
			}
		}
		catch(Exception e){
			connected = false;
		}

		/* TODO:
		 * 1) Create a request message object from the socket input.
		 * 2) If the request is valid, print the host/path requested.
		 * 3) If the request is invalid, print that it's an invalid request.
		 * 4) If there is no more data from the socket or the request was invalid,
		 *    ensure the loop in run() will end.
		 */
	}
	// Handle a new connection.
	@Override
	public void run() {
		System.out.println(client + ": connected");
		connected = true;
		try {
			// setup input stream for the client 
			socketInput = new Scanner(client.getInputStream());

			//keep proccessing requests from the client until either they disconnect or we do 
			while(connected) {
				processRequest();
			}

		} catch(Exception e) {
			System.out.println(client + ": socket error: " + e);
		} finally {
			//close the client connection
			try {
				client.close();
			} catch (IOException e) {}
			System.out.println(client + ": closed");
		}
	}
}


