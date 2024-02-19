package dns;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.lang.NumberFormatException;
import java.util.NoSuchElementException;
import java.io.IOException;
import java.util.Scanner;
/**
 * Class representing a single DNS zone file.
 * @version 1.0
 */
public class DNSZone {

	private HashMap<String, String> mapInput = new HashMap<>();
	private HashMap<String, Boolean> mapBool = new HashMap<>();
	private Scanner inFile; 
	private int ttl;
	private String [] zoned;
	private String zonefile_name; 

	/**
	 * single constructor to make a DNS Zone object given a zone file name
	 * @param zonefile_name the path to a file that should be in zone file format
	 */
	public DNSZone(String zonefile_name) {
		this.zonefile_name = zonefile_name;
		traverseFile();
	}

	private void traverseFile() {
		try{
			inFile = new Scanner(new File(zonefile_name));

			int i = 0;
			while (inFile.hasNextLine())
			{
				if (i ==0) {
					ttl = Integer.valueOf(inFile.nextLine());
					i++;
					continue;
				}
				createArray(inFile.nextLine());
				createMap();
			}

		}catch (IOException e){
			System.out.println(e.getMessage());
		}
	}

	private void createArray(String line){
		zoned = line.split("\\s+");
	}
	private boolean checkType() {
		if (!zoned[2].equals("IN") || !zoned[3].equals("A")) {
			return false;
		}
		return true;
	}

	private void createMap() {
		boolean form = checkType();
		String hostname = zoned[0];
		String ip = zoned[4];
		mapInput.put(hostname, ip);
		mapBool.put(hostname, form);	
	}
	/**
	 * get the global TTL for the entire zone
	 * @return the global TTL
	 */
	public int getTTL() {
		return ttl;
	}
	/**
	 * find a record given the name, type, and class
	 * @param   name    the hostname to lookup
	 * @param   type    the record type to lookup; must be "A"
	 * @param   rclass  the record class to lookup; must be "IN"
	 * @return          null if record doesn't exit or type/class are invalid; the IP (as a string) otherwise
	 */
	public String getRecord(String name, String type, String rclass) {
		if(!mapInput.containsKey(name)){
			return null;
		}else if(mapBool.get(name) == false){
			System.exit(0);
			return null;
		}

		return mapInput.get(name);
	}
}


