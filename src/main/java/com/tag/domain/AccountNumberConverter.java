package com.tag.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;

@Converter
public final class AccountNumberConverter implements AttributeConverter<String, String> {

    private static final String ALGORITHM = "AES";
    private final SecretKeySpec keySpec;

    public AccountNumberConverter(@Value("${encryption.password}") final String password) {
        final byte[] decodedKey = Base64.getDecoder().decode(password);
        this.keySpec = new SecretKeySpec(decodedKey, ALGORITHM);
    }

    private String encrypt(String inputData) throws Exception {
        final Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(inputData.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    private String decrypt(String encryptedData) throws Exception {
        final Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(original);
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return encrypt(attribute);
        } catch (Exception e) {
            throw new RuntimeException("Encryption error", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return decrypt(dbData);
        } catch (Exception e) {
            throw new RuntimeException("Decryption error", e);
        }
    }
}
