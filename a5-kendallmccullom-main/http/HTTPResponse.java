package http;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

/**
 * Class representing a single HTTP response message.
 *
 * @version 1.0
 */
public class HTTPResponse {

	private static String pathString;
	private static Path path;
	private static Map<String, String> mapInput = new HashMap<String, String>();
	private static DataOutputStream socketOutput;
	private static HTTPRequest req;
	private static boolean valid;
	private static boolean validInput;
	private static byte [] mg;

	public HTTPResponse(HTTPRequest req, DataOutputStream socketOut, boolean validInput) {
		this.req = req;
		this.validInput = validInput;
		socketOutput = socketOut;
		valid = true;
		if(checkPath() == true) {
			getType();
			getLength();
			getServer();
			getDate();
		}
	}

	public static boolean verifyPath(){
		return checkPath();
	}

	public static String getStatus(){
		String status = mapInput.get("status");
		status = status.replaceAll("\\r\\n","");
		return status; 
	}

	public static DataOutputStream sendMessage(){
		getMessage();
		return socketOutput;
	}

	private static boolean checkPath() {
		String content = "";
		getPathResponse();
		String status = "";
		if (!validInput) {
			status = "\r\nHTTP/1.1 400 Bad Request";
			content = "errors/400.html";
			valid = false;
		}else if (pathString.equals("/")) {
			content = "content/index.html";
		} else if (pathString.length() !=0) {
			content = "content/"+pathString;
		}
		path = Paths.get(content);
		if (Files.exists(path) && valid) {
			status = "HTTP/1.1 200 OK";
			valid = true;
		}else if (valid){
			status = "HTTP/1.1 404 Not Found";
			content = "errors/404.html";
			valid = false;
		}
		path = Paths.get(content);
		readFile(path);
		mapInput.put("status", status);
		return valid;
	}

	private static void getType() {
		String type = "";
		try {
			type = Files.probeContentType(path);
		} catch (IOException e){type = null;}
		mapInput.put("type", type);
	}

	private static void getServer() {
		String server = "mccullkg";
		mapInput.put("server", server);
	}

	private static void getDate() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
		String format = formatter.format(ZonedDateTime.now(ZoneId.of("GMT")));
		ZonedDateTime get = ZonedDateTime.parse(format, formatter);
		mapInput.put("date", get.format(formatter));
	}

	private static void getLength() {
		long length = 0;
		try {
			length = Files.size(path);
		} catch (IOException e){length = 0;}
		mapInput.put("length", String.valueOf(length));
	}

	private static void getPathResponse() {
		pathString = req.getPath();
	}

	private static void readFile(Path p){
		try {
			mg = Files.readAllBytes(p);
		} catch (IOException e){}
	}

	private static void getMessage() {
		String finalString= "";
		finalString += (mapInput.get("status"));
		if (valid) {
			finalString+= "\r\nServer: " + mapInput.get("server")
				+ "\r\nContent-Length: " + mapInput.get("length")
				+ "\r\nDate: " + mapInput.get("date")
				+"\r\nContent-Type: " + mapInput.get("type");	
		}
		finalString += "\r\n\r\n";
		try {
			socketOutput.writeBytes(finalString);
			socketOutput.write(mg);
		}	catch (IOException e){}
	}

	/*
	 * TODO 1) Create methods and member variables to represent an HTTP response. 
	 * 2)Set response fields based on the request message received. 
	 * 		a) If the request was invalid, send a 400 Bad Request response, with errors/400.html. 
	 * 		b) If the request path doesn't exist, send a 404 Not Found, with errors/404.html. 
	 * 		c) Otherwise, send a 200 OK, with the full contents of the file. 
	 * 		d) Every response must have these four headers: Server, Date, Content-Length, and Content-Type. 
	 * 				i) Note that Date must follow the required HTTP date format. 
	 * 3) Craft the response message and send it out the socket with DataOutputStream.
	 * 		a) Be sure that you use "\r\n" to separate each line.
	 */
}


