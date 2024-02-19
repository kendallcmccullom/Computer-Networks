package echo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPServer {

	public static void main(String[] args) {

		try (
				/* Use port 3363 */        
				var sock = new DatagramSocket(3363);
		    ) {
			/* keep reading packets one at a time, forever */
			while(true) {
				/* use a buffer to hold the incoming bytes */
				var in_buf = new byte[1400];

				/* need a packet to hold the incoming data, stored in in_buf */
				var in_packet = new DatagramPacket(in_buf, in_buf.length);

				/* read one packet in from the socket */
				sock.receive(in_packet);
				System.out.println(in_packet.getSocketAddress());

				/* convert the byte array to a string */
				var in_msg = new String(in_buf, 0, in_packet.getLength());

				/* create a buffer with the response message (same as received message) */
				var out_buf = in_msg.getBytes();

				/* create a response packet, with destination the same as the original sender */
				var out_packet = new DatagramPacket(out_buf, out_buf.length, in_packet.getSocketAddress());

				/* send the response packet */
				sock.send(out_packet);
			}
		} catch(IOException e) {
			/* Have to catch IOexceptions for most socket calls */
			System.out.println("Network error!");
		}
	}
}
