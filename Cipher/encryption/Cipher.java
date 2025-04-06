package encryption;

public interface Cipher {
	/* returns the cipher text version of plainText */
	public String encrypt(String plainText);

	/* returns the plain text version of cipherText */
	public String decrypt(String cipherText);
}
