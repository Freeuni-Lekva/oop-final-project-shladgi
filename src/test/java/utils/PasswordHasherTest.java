package utils;

import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordHasherTest {


    @Test
    public void easyTest(){
        PasswordHasher hasher = new PasswordHasher();
        String salt = hasher.generateSalt();
        String hash = hasher.hashPassword("easypass", salt);
        assertTrue(hasher.verifyPassword("easypass", salt, hash));
        assertFalse(hasher.verifyPassword("easypasss", salt, hash));
        assertFalse(hasher.verifyPassword("easypasss", salt, "falsehash"));

    }

    @Test
    public void hardTest(){
        PasswordHasher hasher = new PasswordHasher();
        String salt = hasher.generateSalt();
        String hash = hasher.hashPassword("Hardpass123..", salt);
        assertTrue(hasher.verifyPassword("Hardpass123..", salt, hash));
        assertFalse(hasher.verifyPassword("Hardpass123..", "falsesalt", hash));

    }
}
