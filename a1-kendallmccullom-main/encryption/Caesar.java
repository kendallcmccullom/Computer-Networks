package encryption;

import java.util.Scanner;

public class Caesar implements Cipher {
	
	
	private String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private String alphabet2;
	

	/* initialize this Caesar object by getting the shift */
	public Caesar(Scanner keyboard) {
		
		System.out.print("Enter the Caesar shift value (0-25): ");
		if(!keyboard.hasNextInt()) {
	           System.out.println("Invalid input! Enter an integer next time. Try again.");
	           System.exit(0);
		}
		
        int shift = keyboard.nextInt();
        if (shift>25 || shift <0) {
        	 System.out.println("Invalid input! Enter an integer 0-25 next time. Try again.");
             System.exit(0);
        }
        
        
		alphabet2 = alphabet.substring(shift)+alphabet.substring(0,shift);
		
	}
	

	

	/* encrypt plainText */
	@Override
	public String encrypt(String plainText) {
		String encryptC = "";
		plainText = plainText.toUpperCase();
		
		for(int i = 0; i<plainText.length(); i++) {
			
			int placement = alphabet.indexOf(plainText.charAt(i));
			if (placement >= 0) {
				encryptC += alphabet2.charAt(placement);
			}
			else {
				encryptC += plainText.charAt(i);
			}
		}
		
		return encryptC;
	}

	/* decrypt cipherText */
	@Override
	public String decrypt(String cipherText) {
		String decryptC = "";
		cipherText = cipherText.toUpperCase();
		
		for(int i = 0; i<cipherText.length(); i++) {
			//System.out.println(plainText.charAt(i));
			
			int placement = alphabet2.indexOf(cipherText.charAt(i));
			if (placement >= 0) {
				decryptC += alphabet.charAt(placement);
			}
			else {
				decryptC += cipherText.charAt(i);
			}
			
		}
		
		return decryptC;
	}
}




