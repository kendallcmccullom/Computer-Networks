package http;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * HTTPConnection represents a single client connection to this server.
 * @version 1.0
 */
public class HTTPConnection implements Runnable {
	//Already connected socket for one client.
	private Socket client;

	//Input channel.
	private Scanner socketInput;

	//Output channel.
	private DataOutputStream socketOutput;

	//Current state of this connection.
	//If EOF or any errors are detected, set this to false.
	private boolean connected;

	/**
	 * Required constructor.
	 * @param client client's socket created in the main thread
	 */
	HTTPConnection(Socket client) {
		this.client = client;
	}

	//Handle a single request message.
	private void processRequest() {
		try {
			var request = new HTTPRequest(socketInput);
			boolean requestValid = request.isValid();
			if (requestValid) {
				String url = request.getHostHeader() + request.getPath();
				System.out.println(client + ": Received valid request for " + url);
			}
			else {
				System.out.println(client + ": Received invalid request");
				connected = false;
			}
			var response = new HTTPResponse(request, socketOutput, requestValid);
			System.out.println(response.getStatus());
			socketOutput = response.sendMessage();
			String finalMessage = socketOutput.toString();
			if (!response.verifyPath()){
				connected = false;
			}
			/* TODO:
			 * 1) Create a response message based on the request.
			 * 2) Print a message with the status code we are sending back.
			 * 3) Send the response out the socket.
			 * 4) If any errors were detected, ensure that this connection will close.
			 */
		} catch (NoSuchElementException e) {
			connected = false;
		}
	}

	//Handle a new connection.
	@Override
	public void run() {
		System.out.println(client + ": connected");
		connected = true;

		try {
			/* setup input and output streams for the client */
			socketInput = new Scanner(client.getInputStream());
			socketOutput = new DataOutputStream(client.getOutputStream());

			/* keep proccessing requests from the client until either
			 * they disconnect or we do */
			while(connected) {
				processRequest();
			}
		} catch(Exception e) {
			System.out.println(client + ": socket error: " + e);
		} finally {
			/* close the client connection */
			try {
				client.close();
			} catch (IOException e) {}
			System.out.println(client + ": closed");
		}
	}
}
