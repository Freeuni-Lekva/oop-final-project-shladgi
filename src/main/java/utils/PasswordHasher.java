package utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class PasswordHasher {
    private static final String characterSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public String generateSalt(){
        Random random = new Random();
        StringBuilder salt = new StringBuilder();
        for(int i = 0; i < 16; i++){
            int randomIndex = random.nextInt(characterSet.length());
            salt.append(characterSet.charAt(randomIndex));
        }
        return salt.toString();
    }

    public String hashPassword(String password, String salt) {
        String passwordSalt = password + salt;

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");

            byte[] hashedBytes = digest.digest(passwordSalt.getBytes(StandardCharsets.UTF_8));

            return hexToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean verifyPassword(String inputPassword, String salt, String storedHash) {
        return storedHash.equals(hashPassword(inputPassword, salt));
    }

    /*
	 Given a byte[] array, produces a hex String,
	 such as "234a6f". with 2 chars for each byte in the array.
	 (provided code)
	*/
    private String hexToString(byte[] bytes) {
        StringBuffer buff = new StringBuffer();
        for (int i=0; i<bytes.length; i++) {
            int val = bytes[i];
            val = val & 0xff;  // remove higher bits, sign
            if (val<16) buff.append('0'); // leading 0
            buff.append(Integer.toString(val, 16));
        }
        return buff.toString();
    }
}
