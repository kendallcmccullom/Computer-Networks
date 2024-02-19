package encryption;

import java.util.Scanner;

public class SecretMessage {
    public static void main(String[] args) {
        /* read from stdin */
        Scanner keyboard = new Scanner(System.in);

        System.out.println("Welcome to the Secret Message Machine!");
        System.out.println();

        /* get the cipher to use */
        System.out.print("Enter 'c' to use a Caesar cipher or 'v' to use a Vigenere cipher: ");
        String cipher = keyboard.nextLine();
        if(!cipher.equals("c") && !cipher.equals("v")) {
            System.out.println("Invalid input! Enter 'c' or 'v' next time. Try again.");
            System.exit(0);
        }
        
        /* get the encryption direction */
        System.out.print("Enter 'e' to encrypt a message  or 'd' to decrypt a message: ");
        String direction = keyboard.nextLine();
        String directionLong = "";
        if(direction.equals("e")) {
            directionLong = "encrypted"; 
        } else if(direction.equals("d")) {
            directionLong = "decrypted"; 
        } else {
            System.out.println("Invalid input! Enter 'e' or 'd' next time. Try again.");
            System.exit(0);
        }

        /* get the message to be encrypted/decrypted */
        System.out.printf("Enter the message to be %s: ", directionLong);
        String message = keyboard.nextLine();

        /* create the appropriate Cipher object */
        /* note that the constructor will prompt for any key info needed for that cipher */
        Cipher myCipher;
        if(cipher.equals("c")) {
            myCipher = new Caesar(keyboard);
        } else {
            myCipher = new Vigenere(keyboard);
        }

        /* print the encrypted/decrypted message */
        String resultMessage;
        if(direction.equals("e")) { 
            resultMessage = myCipher.encrypt(message);
        } else {
            resultMessage = myCipher.decrypt(message);
        }
        System.out.printf("Final %s message: %s%n", directionLong, resultMessage);
    }
}
