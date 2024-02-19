package subnet;
/** 
 * main Class for a Subnet Calculator.
 *
 * @version 1.0
 */
public class Calculator {
	/**
	 * Main method that uses the Subnet class to display subnet information.
	 * @param args subnet in CIDR notation
	 */
	public static void main(String[] args) {

		/* we must have a single argument */
		if(args.length != 1) {
			System.out.println("Usage: java subnet.Calculator IP/PREFIX");
			System.exit(0);
		}
		try {
			var subnet = new Subnet(args[0]);
			System.out.println("IP: " + subnet.getIP());
			System.out.println("Netmask: " + subnet.getNetmask());
			System.out.println("Number Usable IPs: " + subnet.getNumUsableIPs());
			System.out.println("Network Address: " + subnet.getNetworkAddress());
			System.out.println("First Usable Address: " + subnet.getFirstUsableAddress());
			System.out.println("Last Usable Address: " + subnet.getLastUsableAddress());
			System.out.println("Broadcast Address: " + subnet.getBroadcastAddress());
		} catch(Exception e) {
			/* any validation errors will throw an Exception */
			System.out.println("Invalid CIDR notation!");
			System.out.println("Usage: java subnet.SubnetCalculator IP/PREFIX");
			System.exit(0);
		} 
	}
}




