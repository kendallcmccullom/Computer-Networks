package echo;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class TCPServer {

	public static void main(String[] args) {

		try (
				/* Accept connections on port 3363 */        
				var listener = new ServerSocket(3363);
		    ) {
			/* create a thread pool with 16 threads */
			var thread_pool = Executors.newFixedThreadPool(16);

			/* each new connection is handled by one thread */
			while(true) {
				thread_pool.execute(new EchoTask(listener.accept()));
			}
		} catch(IOException e) {
			/* Have to catch IOexceptions for most socket calls */
			System.out.println("Network error!");
		}
	}

	private static class EchoTask implements Runnable {
		private Socket client;

		EchoTask(Socket client) {
			this.client = client;
		}

		@Override
		public void run() {
			System.out.println("Connection: " + client);

			try {
				/* setup input and output streams for the client */
				var sock_in = new Scanner(client.getInputStream());
				var sock_out = new PrintWriter(client.getOutputStream(), true);

				/* read one line of plain text from the client */
				var message = sock_in.nextLine();

				/* send the same line of text back to the client */
				sock_out.println(message);
			} catch(Exception e) {
				System.out.println("Socket Error: " + client);
			} finally {
				/* close the client connection */
				try {
					client.close();
				} catch (IOException e) {}
				System.out.println("Closed: " + client);
			}
		}
	}
}
