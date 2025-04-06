package echo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
import java.util.DoubleSummaryStatistics;

public class UDPClient {

	public static void main(String[] args) {
		try (
				/* open a socket on any unused UDP port */
				var socket = new DatagramSocket();
		    ) {
			/* get a message from the user */
			var stdin = new Scanner(System.in);
			System.out.print("Enter a message: ");
			var message = stdin.nextLine();

			/* put the message into a buffer */
			var out_buf = message.getBytes();

			/* get the localhost address, set the destination port */
			//var dest_ip = InetAddress.getLocalHost();
			var dest_ip= InetAddress.getByName("74.207.233.127");

			var dest_port = 3363;

			/* put the buffer into a packet, with destination address and port */
			var out_packet = new DatagramPacket(out_buf, out_buf.length, dest_ip, dest_port);

			/* need a buffer to hold the response */
			var in_buf = new byte[1400];

			/* need a packet to hold the response */
			var in_packet = new DatagramPacket(in_buf, in_buf.length);

			socket.send(out_packet);
			socket.receive(in_packet);

			String output = getOutput(socket, in_packet, out_packet);

			System.out.println(output);

			/* convert the byte array to a string */
			var in_msg = new String(in_buf, 0, in_packet.getLength());

			/* print the server's response to the screen */
			//System.out.println(in_msg);

		} catch(IOException e) {
			/* Have to catch IOexceptions for most socket calls */
			System.out.println("Network error!");
		}
	}

	private static String getOutput(DatagramSocket socket, DatagramPacket  in_packet, DatagramPacket out_packet){
		try{
			int rep = 10;
			double start_time = 0; 
			double end_time = 0;
			double [] stats = new double[rep];

			for (int i = 0; i<rep; i++){
				double total_time = 0;
				start_time = System.nanoTime();
				socket.send(out_packet);
				socket.receive(in_packet);
				end_time = System.nanoTime();
				total_time = (end_time-start_time);
				stats[i] = total_time;
			}

			DoubleSummaryStatistics dss= new DoubleSummaryStatistics();
			for (double d: stats) dss.accept(d);

			double max = dss.getMax();
			double min = dss.getMin();
			double mean = dss.getAverage();
			double sum = 0;

			for (int i = 0; i<rep; i++){
				double datapoint = stats[i];
				double dist = Math.pow(Math.abs(mean-datapoint),2);
				sum += dist;
			}
			double std = Math.pow((sum/rep),(0.5));
			
			String finalOutput = ("Min: " + form(min) + 
					"\nMean: " + form(mean) + 
					"\nMax: " + form(max) + 
					"\nStandard Deviation: " + form(std));

			return finalOutput;
		} catch(IOException e) {
			return ("Network error!");
		}

	}

	private static String form(double datapoint){
		datapoint = datapoint/Math.pow(10,6);
		String formatted = String.format("%.3f", datapoint);
		return (formatted + " ms");
	}
}
