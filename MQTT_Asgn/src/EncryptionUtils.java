import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.util.Base64;

public class EncryptionUtils {
    private static final String KEY_FILE = "encryption.key";

    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128); // 128-bit key
        return keyGen.generateKey();
    }

    public static void saveKey(SecretKey key) throws Exception {
        byte[] encodedKey = key.getEncoded();
        try (FileOutputStream fos = new FileOutputStream(KEY_FILE)) {
            fos.write(encodedKey);
        }
    }

    public static SecretKey loadKey() throws Exception {
        File keyFile = new File(KEY_FILE);
        if (!keyFile.exists()) {
            throw new FileNotFoundException("Encryption key file not found: " + KEY_FILE);
        }
        byte[] encodedKey;
        try (FileInputStream fis = new FileInputStream(KEY_FILE)) {
            encodedKey = fis.readAllBytes();
        }
        return new SecretKeySpec(encodedKey, "AES");
    }

    public static String encrypt(String data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String encryptedData, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decoded = Base64.getDecoder().decode(encryptedData);
        byte[] original = cipher.doFinal(decoded);
        return new String(original);
    }
}
