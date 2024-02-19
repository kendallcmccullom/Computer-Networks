package dns;

import java.net.DatagramPacket;
import java.util.HashMap;

/**
 * Class representing a single DNS message.
 * @version 1.0
 */
public class DNSMessage {
	// max length of a DNS message is 512 bytes
	final private static int MAX_DNS_MSG_LENGTH = 512;

	// the UDP packet provided to the constructor
	private DatagramPacket pkt;
	private byte[] data;
	private int data_length;

	// the DNS message ID field
	private int id;

	// the DNS message Flags field, with individual flags parsed out
	private int flags;
	private int flag_qr;
	private int flag_opcode;
	private int flag_aa;
	private int flag_tc;
	private int flag_rd;
	private int flag_ra;
	private int flag_rcode;

	// the four DNS Message fields for the # of questions, answers, and RRs
	private int num_questions;
	private int num_answers;
	private int num_auth_rrs;
	private int num_additional_rrs;

	// map the various required Class numbers to their human readable names
	private static HashMap<Integer, String> classes;

	// map the various required Type numbers to their human readable names
	private static HashMap<Integer, String> types;

	// use a static initialization block to create the above maps
	static {
		classes = new HashMap<Integer, String>();
		classes.put(1, "IN");

		types = new HashMap<Integer, String>();
		types.put(1, "A");
		types.put(2, "NS");
		types.put(5, "CNAME");
		types.put(6, "SOA");
		types.put(12, "PTR");
		types.put(28, "AAAA");
	}

	// the DNS message fields to represent a single question (including type and
	// class as both numbers and strings
	private String question_name;
	private int question_type;
	private String question_type_str;
	private int question_class;
	private String question_class_str;

	private int rdLength; // check to make sure this resets everytime it is used
	private String rdata;
	private byte[] response_message;
	private int ttl;
	private DNSMessage request;
	private int endIndex;
	private int numAns = 1;
	private int numAuth = 0;
	private int numAdd = 0;
	private int endCopy;

	/* TODO: add variables for the answer section */

	/**
	 * constructor to make a DNS Message object given a UDP packet
	 * @param pkt a UDP packet that should contain a DNS message
	 */
	public DNSMessage(DatagramPacket pkt) {
		this.pkt = pkt;
		parseHeader();
		parseFlags();
		parseQuestions();
	}

	/**
	 * constructor to make a DNS Message response object given a DNS request message and record information
	 * @param request a complete DNS request message
	 * @param rdata   the rdata field to send in the answer, obtained from the zone
	 * @param ttl     the ttl field to send in the answer, obtained from the zone
	 */
	public DNSMessage(DNSMessage request, String rdata, int ttl) {
		this.ttl = ttl;
		this.rdata = rdata;	
		this.request = request;
		data = request.data;
		data_length = request.data_length;
		response_message = new byte[MAX_DNS_MSG_LENGTH];
		getAnswer();
		data = response_message;
		data_length = endIndex;
	}
	/* TODO: fill me in (and use (lots!) of private methods) */
	/* be sure to read the assignment document for important details */
	private void getAnswer() {
		endIndex = getEndIndex();
		loopingExisting();
		if(rdata !=null){
			getName();
			getTTL();
			getLength();
			getDataAnswer();
		}
		id = request.id;
		num_questions = request.num_questions;
		question_name = request.question_name;
		question_type_str = request.question_type_str;
		question_class_str = request.question_class_str;
	}

	private int getEndIndex(){
		int i = 12;
		while(true){
			i++;
			if (data[i]==0)
				return i+5;
		}
	}

	private String[] parseIP() {
		String[] arrIP = rdata.split("\\.");
		return arrIP;
	}

	private void getTTL() {
		breakInt(ttl, 4, endIndex);
	}

	private void getDataAnswer() {
		if(check_Ip()){
			String[] ipAdd = parseIP();
			for (int i = 0; i < 4; i++) {
				int ipInt = Integer.valueOf(ipAdd[i]);
				response_message[endIndex] = (byte) ipInt;
				endIndex += 1;
			}
		}
	}

	private void breakInt(int numVal, int numBytes, int index) {
		for (int i = numBytes; i > 0; i -= 2) {
			int shiftVal = numVal >> (i - 2) * 8;
			response_message[index] = (byte) (shiftVal / Math.pow(2, 8));
			increaseEndIndex(index == endIndex);
			response_message[index+1] = (byte) (shiftVal % Math.pow(2, 8));
			increaseEndIndex(index+1 == endIndex);
			index+=2;
		}
	}

	private void increaseEndIndex(boolean atEnd) {
		if(atEnd) {
			endIndex += 1;
		}
	}

	private void getLength() {
		rdLength = 4;
		breakInt(rdLength, 2, endIndex);
	}

	private void getName() {
		response_message[endIndex] = (byte) (Math.pow(2,7) + Math.pow(2,6));
		endIndex++;
		response_message[endIndex] = (byte) 12;
		endIndex++;	
		getTypeResp();
		getClassResp();
	}

	private void getTypeResp(){
		breakInt (request.question_type, 2, endIndex);
	}

	private void getClassResp(){
		breakInt(request.question_class, 2, endIndex);
	}	       

	private void getNumAnswers() {
		num_answers = numAns;
		breakInt(numAns,2,6);
	}

	private void getNumAuth() {
		num_auth_rrs = numAuth;
		breakInt(numAuth, 2, 8);
	}

	private void getNumAdd() {
		num_additional_rrs = numAdd;
		breakInt(numAdd, 2, 10);
	}

	private void loopingExisting() {
		updateFlag();
		getNumAnswers();
		getNumAuth();
		getNumAdd();
		for (int i = 0; i < endIndex; i++) {
			if (i ==1 || i == 0 || i == 4 || i ==5 || (i>=12 && i<endIndex)) {
				response_message[i] = data[i];
			}
		}
	}

	private boolean check_Ip() {
		if (rdata == null) {
			flag_rcode = 3;
			numAns = 0;
			return false;
		} else {
			flag_rcode = 0;
			num_answers = 1;
			return true;
		}
	}

	private void updateFlag() {
		check_Ip();
		flag_qr = flag_aa = 1;
		flag_tc = flag_ra = 0;
		int flag_z = 0;
		flag_rd = request.flag_rd;
		flag_opcode = request.flag_opcode;

		int[] vals = { flag_qr, flag_opcode, flag_aa, flag_tc, flag_rd, flag_ra, flag_z, flag_rcode };
		int sum = 0;
		int index = 0;
		for (int i = 15; i >= 6; i--) {
			int pow = (int) Math.pow(2, i);
			sum += (pow * vals[index]);

			index += 1;
			if (i == 14) {
				i -= 3;
			}
		}
		sum+=flag_rcode;
		flags = sum;
		response_message[2] = (byte) (sum >> 8);
		response_message[3] = (byte) (sum % (int) Math.pow(2, 8));
	}

	/**
	 * utility method to convert two bytes into a single short (16-bit) value
	 * @param b0 the first/left/top bits
	 * @param b1 the second/right/bottom bits
	 */
	private int bytesToShort(byte b0, byte b1) {
		int i0 = b0 & 0xff;
		int i1 = b1 & 0xff;
		return (i0 << 8) | i1;
	}

	//utility method to parse out the id, flags, and # fields from the UDP packet
	private void parseHeader() {
		// get the packet data a byte[]
		data = pkt.getData();

		// grab the length for now, though we don't need it yet
		data_length = pkt.getLength();

		// the first 12 bytes in the message are the 6 2-byte fields that start the message
		id = bytesToShort(data[0], data[1]);
		flags = bytesToShort(data[2], data[3]);
		num_questions = bytesToShort(data[4], data[5]);
		num_answers = bytesToShort(data[6], data[7]);
		num_auth_rrs = bytesToShort(data[8], data[9]);
		num_additional_rrs = bytesToShort(data[10], data[11]);
	}

	//utility method to parse the flags into individual flag variables
	private void parseFlags() {
		// see the documentation for individual flag positions within the 16-bit field
		flag_qr = flags >> 15 & 0x1;
		flag_opcode = flags >> 11 & 0xf;
		flag_aa = flags >> 10 & 0x1;
		flag_tc = flags >> 9 & 0x1;
		flag_rd = flags >> 8 & 0x1;
		flag_ra = flags >> 7 & 0x1;
		flag_rcode = flags & 0xf;
	}

	//utility method to parse out the questions section
	private void parseQuestions() {
		// for our server, we only support a single question
		if (num_questions != 1) {
			System.out.println("Warning, unexpected number of questions.");
			return;
		}

		// the name is stored as a series of one-byte lengths followed by that many
		// chars, each representing a single label

		// build up the name as we go
		question_name = "";

		// the first byte of the name starts after the last # field in the message
		int next_byte = 12;

		// get the length of the next label from the message
		int next_label_len = data[next_byte];

		// the name is complete whtn the final length field is zero
		while (next_label_len != 0) {

			// read the number of bytes out of the message corresponding to the length
			int i;
			for (i = next_byte + 1; i <= next_byte + next_label_len; i++) {
				question_name += (char) data[i];
			}

			// each label is separated by a dot
			question_name += ".";

			// move on to the next label
			next_byte = i;
			next_label_len = data[next_byte];
		}

		// the above loop adds the trailing dot, so let's remove that
		question_name = question_name.substring(0, question_name.length() - 1);


		// after the name, the question type and class are both 2-byte values
		question_type = bytesToShort(data[next_byte + 1], data[next_byte + 2]);
		question_class = bytesToShort(data[next_byte + 3], data[next_byte + 4]);

		endCopy = next_byte+4;
		// get the string version of the question type
		if (types.containsKey(question_type)) {
			question_type_str = types.get(question_type);
		} else {
			question_type_str = String.format("%d", question_type);
		}

		// get the string version of the question class
		if (classes.containsKey(question_class)) {
			question_class_str = classes.get(question_class);
		} else {
			question_class_str = String.format("%d", question_class);
		}

	}

	/**
	 * return a string version of this message
	 * @return the String version of this message
	 */
	public String toString() {
		// use StringBuilder because many String concatenations is inefficient in Java
		var sb = new StringBuilder();

		// add the inital 6 fields
		sb.append(String.format("ID: 0x%04X%n", id));
		sb.append(String.format("Flags: 0x%04X%n", flags));
		if (flag_qr == 0 && flag_opcode == 0) {
			sb.append(String.format("- Standard Query%n"));
		} else if (flag_qr == 1 && flag_rcode == 0) {
			sb.append(String.format("- Standard Response%n"));
		} else if (flag_qr == 1 && flag_rcode == 3) {
			sb.append(String.format("- Response NXDomain%n"));
		} else {
			sb.append(String.format("- Unexpected QR/opcode%n"));
		}
		if (flag_rd == 1) {
			sb.append(String.format("- Recursion Requested%n"));
		}
		if (flag_ra == 1) {
			sb.append(String.format("- Recursion Available%n"));
		}
		sb.append(String.format("# Questions: %d%n", num_questions));
		sb.append(String.format("# Answers: %d%n", num_answers));
		sb.append(String.format("# Authority RRs: %d%n", num_auth_rrs));
		sb.append(String.format("# Additional RRs: %d%n", num_additional_rrs));

		// add the question section if there is a question
		if (num_questions == 1) {
			sb.append(String.format("Questions:%n"));
			sb.append(String.format("- %s, %s, %s%n", question_name, question_type_str, question_class_str));
		}

		/* TODO: add the answers section to the output */
		if (num_answers == 1) {
			sb.append(String.format("Answers:%n"));
			sb.append(String.format("- %s, %s, %s, %d, %s%n", question_name, question_type_str, question_class_str, ttl, rdata));
		}

		return sb.toString();
	}

	/**
	 * accessor for the name in the question section
	 * @return the name in the question section
	 */
	public String getQuestionName() {
		return question_name;
	}

	/**
	 * accessor for the type in the question section
	 * @return the type, as a string, in the question section
	 */
	public String getQuestionType() {
		return question_type_str;
	}

	/**
	 * accessor for the class in the question section
	 * @return the class, as a string, in the question section
	 */
	public String getQuestionClass() {
		return question_class_str;
	}

	/**
	 * accessor for byte buffer for this entire message
	 * @return the full byte buffer representation of this message
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * accessor for byte buffer length for this message
	 * @return the number of bytes in the full byte buffer representation of this message
	 */
	public int getDataLength() {
		return data_length;
	}
}



