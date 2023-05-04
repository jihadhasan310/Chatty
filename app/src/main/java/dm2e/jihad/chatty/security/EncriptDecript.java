package dm2e.jihad.chatty.security;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncriptDecript {
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_AUTHENTICATION_TAG_LENGTH = 128;
    public static final String AES_KEY = "83fe1a7f12e48b4c4a08d4a90f2c4b0d";

    public static String encrypt(String message) throws Exception {
        byte[] iv = generateIV();
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] keyBytes = AES_KEY.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_AUTHENTICATION_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmParameterSpec);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
        byte[] encryptedMessage = new byte[GCM_IV_LENGTH + encryptedBytes.length];
        System.arraycopy(iv, 0, encryptedMessage, 0, GCM_IV_LENGTH);
        System.arraycopy(encryptedBytes, 0, encryptedMessage, GCM_IV_LENGTH, encryptedBytes.length);
        return Base64.getEncoder().encodeToString(encryptedMessage);
    }

    public static String decrypt(String encryptedMessage) throws Exception {
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedMessage);
        byte[] iv = new byte[GCM_IV_LENGTH];
        System.arraycopy(encryptedBytes, 0, iv, 0, GCM_IV_LENGTH);
        byte[] encryptedMessageBytes = new byte[encryptedBytes.length - GCM_IV_LENGTH];
        System.arraycopy(encryptedBytes, GCM_IV_LENGTH, encryptedMessageBytes, 0, encryptedMessageBytes.length);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] keyBytes = AES_KEY.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_AUTHENTICATION_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmParameterSpec);
        byte[] decryptedBytes = cipher.doFinal(encryptedMessageBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    private static byte[] generateIV() {
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);
        return iv;
    }
}
