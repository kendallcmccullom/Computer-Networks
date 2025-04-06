package subnet;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Class to represent a subnet.
 * @version 1.0
 */
public class Subnet {

	private static String address;
	private static long IPAdd;
	private static long netmask;
	private static long networkAdd;
	private static long broadAdd;
	private static long firstAdd;
	private static int prefix;

	public Subnet(String checkAdd) {
		address = checkForm(checkAdd);
		set();
	}

	public static String getIP() {
		int prefixLoc = address.indexOf("/");
		return address.substring(0,prefixLoc);
	}

	public static String getNetmask() {
		long length = (long) Math.pow(2, 32);
		long net  = ((length - 1) << (32 - prefix)) % length;
		return getString(net);
	}

	public static String getNumUsableIPs() {
		long usables = 32 - prefix;
		if (usables == 0) {
			return String.valueOf(0);
		}
		return String.valueOf((long) ((Math.pow(2, usables))-2));
	}

	public static String getNetworkAddress() {
		return getString(netmask & IPAdd);
	}

	public static String getFirstUsableAddress() {
		if (!prefixSizeCheck()) {
			return "N/A";
		}
		return getString(networkAdd+1);
	}

	public static String getLastUsableAddress() {
		if (!prefixSizeCheck()) {
			return "N/A";
		}
		return getString(broadAdd-1);
	}

	public static String getBroadcastAddress() {
		long netAdd;
		if (!prefixSizeCheck()) {
			netAdd = IPAdd;
		} else {
			netAdd = firstAdd;
		}
		int usableIndex = 32 - prefix;
		long broadLong = (long) Math.pow(2, usableIndex) - 1;
		return getString(netAdd | broadLong);
	}

	private static int getPrefix(String addr) {
		int prefixLoc = addr.indexOf("/");
		return Integer.parseInt(addr.substring(prefixLoc + 1));
	}

	private static long getLong(String addString) {
		int segments = 3;
		long longIP = 0;
		String segString = "";
		for (int i = 0; i < addString.length(); i++) {
			if (addString.charAt(i) == '.' || i == addString.length() - 1) {
				if (i == addString.length() - 1) {
					segString += addString.charAt(i);
				}
				int getval = Integer.parseInt(segString);
				longIP +=((long) getval) << 8 * segments;
				segments--;
				segString = "";
				continue;
			}
			segString += addString.charAt(i);
		}
		return longIP;
	}

	private static String getString(long addLong) {
		long segment = 0;
		String stringIP = "";
		for (int i = 3; i >= 0; i--) {
			segment = addLong / (long) Math.pow(2, (8 * i));
			stringIP += String.valueOf(segment);
			addLong = addLong % ((long) Math.pow(2, (8 * i)));
			if (i != 0) {
				stringIP += ".";
			}
		}
		return stringIP;
	}

	private static String checkForm(String checkingAdd){
		if (checkingAdd.matches(".*[0-9]+.*") || checkingAdd.matches(".*[/]+.*") || checkingAdd.matches(".*[.]+.*")) {
			boolean yesMatch = checkingAdd.matches( "\\b\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}/\\d{1,2}\\b");
			if (yesMatch) {
				prefix = getPrefix(checkingAdd);
				if (prefix< 33) {
					String regex = "\\d{1,3}";
					Pattern digits = Pattern.compile(regex);
					Matcher match = digits.matcher(checkingAdd);
					int count=0;
					while (match.find()&&count<4) {
						count++;
						int value = Integer.parseInt(match.group());
						if (value>255){
							return null;
						}
						if (value<256&&count==4) {
							return checkingAdd;
						}
					}
				}
			}
		}
		return null;
	}

        private static void set(){
                if (!address.equals(null)){
             		IPAdd = getLong(getIP());
                 	netmask = getLong(getNetmask());
                  	networkAdd = getLong(getNetworkAddress());
			if (prefixSizeCheck()){
				firstAdd = getLong(getFirstUsableAddress());
			}
			broadAdd = getLong(getBroadcastAddress());
		}
        }


	private static boolean prefixSizeCheck(){
		if (prefix == 32 || prefix==31){
			return false;
		}
		return true;
	}

}


