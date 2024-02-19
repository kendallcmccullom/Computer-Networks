package dns;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DNSMessage {
	private static int increment;
	private static HashMap<Integer, String> mapInput = new HashMap<>();
	private static String[] labels = { "ID: ", "Flags: ", "# Questions: ", "# Answers: ", "# Authority RRs: ","# Additonal RRs: ", "Questions: " };
	private static List<String> flags;
	private static String questionInfo;
	private static boolean nameFinished;
	private static String name;
	private static byte [] inputStream;
	private static int endIndex;

	public DNSMessage(DatagramPacket packet) {
		inputStream = packet.getData();
		endIndex = 0;
		nameFinished = false;
		flags = new ArrayList<String>();
		name = "";
		parse();
	}// recursion message, printn hex, convert class and type into names

	@Override
	public String toString() {
		String message = "";
		for (int i = 0; i < mapInput.size(); i++) {
			message += mapInput.get(i) +"\n";
			if (i == 1) {
				for (int j = 0; j < flags.size(); j++) {
					message+=flags.get(j)+"\n";
				}
			}
			if (i == mapInput.size() - 1) {
				message += (questionInfo);
			}
		}
		return message;
	}

	private static void parse() {
		int end = inputStream.length;
		for (int i = 0; i < end; i += increment) {
			//when looking for two bytes at a time
			if (i < 12 || nameFinished) {
				increment = 2;
				byte[] array = Arrays.copyOfRange(inputStream, i, i + increment);
				int intbin = concatenate(array);
				if (i == 0 || i == 2) {
					getHex(i, intbin);
				} else if (i == 4) {
					if (!questions(intbin)) {
						end = 12;
					} else{
						questionInfo = "- ";
					}
				} else if (nameFinished) {
					if(i<endIndex){
						questionInfo += formatEnd(i, intbin);
					}else{
						return;
					}
				} else {
					mapInput.put(i / 2, labels[i / 2] + intbin);
				}
			} else{ //byte by byte
				increment = 1;
				//skipping 12 because that byte is not part of the string
				if (i == 12){
					continue;
				}
				getQuestion(i);
			}
		}
	}

	private static String formatEnd(int index, int intbin) {
		if(index<endIndex-2) {
			return name + ", " + getType(intbin) + ", ";
		}else {
			mapInput.put(6, labels[6]);
			return getClass(intbin);
		}
	}

	private static void getQuestion(int index) {
		if (inputStream[index] == 0) {
			nameFinished = true;
			endIndex = index+4;
			return;
		}else if (inputStream[index] == 3) {
			name += ".";
		}else if (inputStream[index] >= 32 && !nameFinished) {
			name += (char) inputStream[index];
		}
	}

	private static void getHex(int index, int intbin) {
		String hexadecimal = String.format("%04x", intbin);
		mapInput.put(index / 2, labels[index / 2] + "0x" + hexadecimal);
		if (index == 2) {
			int intHex = Integer.parseInt(hexadecimal, 16);
			parseFlag(intHex);
		}
	}

	private static String getClass(int num) {
		if (num == 1) {
			return "IN";
		}
		return String.valueOf(num);
	}

	private static String getType(int num) {
		if (num == 1) {
			return "A";
		} else if (num == 2) {
			return "NS";
		} else if (num == 5) {
			return "CNAME";
		} else if (num == 6) {
			return "SOA";
		} else if (num == 12) {
			return "PTR";
		} else if (num == 28) {
			return "AAAA";
		}
		return String.valueOf(num);
	}

	private static boolean questions(int quest) {
		if (quest == 1) {
			mapInput.put(2, "# Questions: " + quest);
			return true;
		} else {
			mapInput.put(2, "There should only be 1 Question");
			return false;
		}
	}

	private static void parseFlag(int index) {
		if (index < Math.pow(2, 11)-1 && index < Math.pow(2, 15)-1) {
			flags.add("- Standard Query");
		} else {
			flags.add("QR/OP Unexpected");
		}
		if ((index>>8)%2 == 1) {
			flags.add("- Recursion Requested");
		}
	}

	private static int concatenate(byte [] subArray) {
		return ((subArray[0]&0xFF) << 8) + (subArray[1]&0xFF);
	}
}

