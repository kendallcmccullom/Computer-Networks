package http;

import java.util.HashMap;
import java.util.Scanner;
/**
 * Class representing a single HTTP request message.
 *
 * @version 1.0
 */
public class HTTPRequest {

	private static HashMap<String, String> variables = new HashMap<>();
	private static String requestLine;
	private static Scanner input; 

	/*
	 * TODO 
	 * 1) Create methods and member variables to represent an HTTP request. 
	 * 		a)There must not be any output messages from this class (no print calls). 
	 * 		b)The class must have at least the following public methods in addition to the
	 * 		constructor: - boolean isValid() - String getPath() - String getHostHeader()
	 * 2) Read a message from the socket. 
	 * 3) Validate the message, and parse out the various header fields. 
	 * 		a) Must have exactly three values on the request line.
	 * 		b) Only support the GET action; any others are errors. 
	 * 		c) Version must be HTTP/1.1; any others are errors. 
	 * 		d) Path must start with a "/". 
	 * 		e) Each header variable line is of the form (variable: value). 
	 * 		f) There must be a Host header. 
	 * 4) Any errors mean that this request is not valid.
	 */
	public HTTPRequest(Scanner inputStream) {
		input = inputStream;
	}

	// returns whether or not the request was valid
	public static boolean isValid() {
		requestLine = input.nextLine();
		if (!checkFirst(requestLine)) {
			return false;
		}
		return checkHeader();
	}

	// returns the Path from a (valid) request message
	public static String getPath() {
		return setPath();
	}
	// returns the Host header variable from a (valid) request message
	public static String getHostHeader() {
		return setHostHeader();
	}

	private static String setPath(){
		int start = requestLine.indexOf("/");
                int end = requestLine.lastIndexOf("H");
                return requestLine.substring(start, end);
	}

	private static String setHostHeader(){
		String host = variables.get("Host:");
                return host;
	}

	private static boolean checkHeader(){
		boolean host = false;
		while (true) {
			String inputLine = input.nextLine();

			if(inputLine.isEmpty()){
				break;
			}

			int colonIndex = inputLine.indexOf(":")+1;
			if (colonIndex<1 || inputLine.charAt(colonIndex) != (' ')) {
				return false;
			}

			String key = inputLine.substring(0,colonIndex);
			String value = inputLine.substring(colonIndex+1);
			variables.put(key, value);
			if (key.equals("Host:")) {
				host = true;
			}

		}
		if (!host) {
			return false;
		}
		return true;
	}

	private static boolean checkFirst(String request){
		String formatRequestLine = "GET \\/($*|\\S+) HTTP/1.1";
		if (request.matches(formatRequestLine)) {
			return true;
		}
		return false;
	}
}


