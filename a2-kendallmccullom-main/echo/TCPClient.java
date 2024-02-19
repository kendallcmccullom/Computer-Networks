package echo;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient {

	public static void main(String[] args) {
		try (
				/* open a connection localhost:3363 */
				//var socket = new Socket("localhost", 3363);
				var socket = new Socket("74.207.233.127", 3363);

				/* setup input and output streams for the socket */
				var sock_in = new Scanner(socket.getInputStream());
				var sock_out = new PrintWriter(socket.getOutputStream(), true);
		    ) {
			/* get a message from the user */
			var stdin = new Scanner(System.in);
			System.out.print("Enter a message: ");
			var message = stdin.nextLine();

			/* send the message to the server */
			sock_out.println(message);

			/* read a single line response from the server */
			var response = sock_in.nextLine();

			/* print the server's response to the screen */
			System.out.println(response);

		} catch(Exception e) {
			/* Have to catch exceptions for most socket calls */
			System.out.println("Network error!");
		}
	}
}

