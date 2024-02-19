package encryption;
import java.util.Scanner;
public class Vigenere implements Cipher {
	
	private String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private String key;
	private int rotation = 0;
    /* initialize this Vigenere object by getting the key string */
    public Vigenere(Scanner keyboard) { 
    	
		System.out.print("Enter key: ");
		
		key = keyboard.nextLine().toUpperCase();
		if (key.isEmpty()){
			System.out.println("Invalid input! key must be at least one letter long. Try again.");
			System.exit(0);
		}
		
		
		if (!key.matches("[A-Z]+")) {
			System.out.println("Invalid input! key must only have letters. Try again.");
			System.exit(0);
		}
		keyboard.close();
		
    }
    /* encrypt plainText */
    @Override public String encrypt(String plainText) {
    	String encryptV = "";
    	plainText = plainText.toUpperCase();
    	
    	for (int i = 0; i<plainText.length(); i++) {
    	
    		int shift = alphabet.indexOf(key.charAt(rotation));
    		
    		String alphashift =alphabet.substring(shift)+ alphabet.substring(0,shift);
    		int placement = alphabet.indexOf(plainText.charAt(i));
    		
    		if (placement >= 0) {
    			
				encryptV += Character.toUpperCase(alphashift.charAt(placement));
				rotation +=1;
			}
			else {
				char adding = plainText.charAt(i);
				encryptV += Character.toUpperCase(adding);
			}
    		
    		
    		
    		if (rotation == key.length()) {
    			rotation = 0;
    		}
    		
    	}
    	
        return encryptV;
    }
    /* decrypt cipherText */
    @Override public String decrypt(String cipherText) {
    	String decryptV = "";
    	cipherText = cipherText.toUpperCase();
    	
    	for (int i = 0; i<cipherText.length(); i++)
    	{
    		int shift = alphabet.indexOf(key.charAt(rotation));
    		String alphashift =alphabet.substring(shift)+ alphabet.substring(0,shift);
    		int placement = alphashift.indexOf(cipherText.charAt(i));
			if (placement >= 0) {
				decryptV += alphabet.charAt(placement);
				rotation +=1;
			}
			else {
				decryptV += cipherText.charAt(i);
			}
    		
    		
    		
    		if (rotation == key.length()) {
    			rotation = 0;
    		}
    		
    	}
    	
        return decryptV;
    }
}


