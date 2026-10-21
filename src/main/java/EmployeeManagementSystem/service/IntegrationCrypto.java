package EmployeeManagementSystem.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/** AES-256-GCM, with owner/provider binding. A key must be provisioned outside source control. */
public final class IntegrationCrypto {
    private static final SecureRandom RANDOM = new SecureRandom();
    private IntegrationCrypto() {}
    public static String randomToken() {
        byte[] bytes = new byte[32]; RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    public static String sha256(String value) {
        return HexFormat.of().formatHex(digest(value));
    }
    public static String pkce(String verifier) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest(verifier));
    }
    private static byte[] digest(String value) {
        try { return MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)); }
        catch (Exception e) { throw new IllegalStateException("SHA-256 unavailable", e); }
    }
    public static void validateKey(String key) { key(key); }
    private static SecretKeySpec key(String encoded) {
        try {
            byte[] bytes = Base64.getDecoder().decode(encoded == null ? "" : encoded);
            if (bytes.length != 32) throw new IllegalArgumentException();
            return new SecretKeySpec(bytes, "AES");
        } catch (IllegalArgumentException ex) { throw new IllegalStateException("Configure a base64-encoded 32-byte calendar encryption key"); }
    }
    public static String encrypt(String plaintext, String encodedKey, String context) {
        if (plaintext == null) return null;
        try {
            byte[] iv = new byte[12]; RANDOM.nextBytes(iv);
            Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
            c.init(Cipher.ENCRYPT_MODE, key(encodedKey), new GCMParameterSpec(128, iv));
            c.updateAAD(context.getBytes(StandardCharsets.UTF_8));
            byte[] sealed = c.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return "v1." + Base64.getEncoder().encodeToString(iv) + "." + Base64.getEncoder().encodeToString(sealed);
        } catch (Exception e) { throw new IllegalStateException("Token encryption failed", e); }
    }
    public static String decrypt(String sealed, String encodedKey, String context) {
        if (sealed == null) return null;
        try {
            String[] parts = sealed.split("\\.");
            if (parts.length != 3 || !"v1".equals(parts[0])) throw new IllegalArgumentException();
            byte[] iv = Base64.getDecoder().decode(parts[1]);
            if (iv.length != 12) throw new IllegalArgumentException();
            Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
            c.init(Cipher.DECRYPT_MODE, key(encodedKey), new GCMParameterSpec(128, iv));
            c.updateAAD(context.getBytes(StandardCharsets.UTF_8));
            return new String(c.doFinal(Base64.getDecoder().decode(parts[2])), StandardCharsets.UTF_8);
        } catch (Exception e) { throw new IllegalStateException("Stored token cannot be decrypted; check key or reconnect", e); }
    }
}
