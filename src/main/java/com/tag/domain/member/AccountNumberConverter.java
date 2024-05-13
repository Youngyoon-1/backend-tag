package com.tag.domain.member;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.security.Key;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;

@Converter
public final class AccountNumberConverter implements AttributeConverter<String, String> {

    private static final String ALGORITHM = "AES";

    private static final String FAIL_INIT_CIPHER = "암호화 설정에 실패했습니다.";
    private static final String FAIL_ENCRYPT = "계좌번호 암호화에 실패했습니다.";
    private static final String FAIL_DECRYPT = "계좌번호 복호화에 실패했습니다.";

    private final Cipher encryptionCipher;
    private final Cipher decryptionCipher;

    public AccountNumberConverter(@Value("${encryption.password}") final String password) {
        final byte[] decodedKey = Base64.getDecoder().decode(password);
        final Key keySpec = new SecretKeySpec(decodedKey, ALGORITHM);
        try {
            encryptionCipher = Cipher.getInstance(ALGORITHM);
            encryptionCipher.init(Cipher.ENCRYPT_MODE, keySpec);
            decryptionCipher = Cipher.getInstance(ALGORITHM);
            decryptionCipher.init(Cipher.DECRYPT_MODE, keySpec);
        } catch (final Exception e) {
            throw new IllegalArgumentException(FAIL_INIT_CIPHER, e);
        }
    }

    @Override
    public String convertToDatabaseColumn(final String accountNumber) {
        if (accountNumber == null) {
            return null;
        }
        try {
            return encrypt(accountNumber);
        } catch (final Exception e) {
            throw new IllegalArgumentException(FAIL_ENCRYPT, e);
        }
    }

    private String encrypt(final String accountNumber) throws Exception {
        byte[] encrypted = encryptionCipher.doFinal(accountNumber.getBytes());
        return Base64.getEncoder()
                .encodeToString(encrypted);
    }

    @Override
    public String convertToEntityAttribute(String encryptedAccountNumber) {
        if (encryptedAccountNumber == null) {
            return null;
        }
        try {
            return decrypt(encryptedAccountNumber);
        } catch (final Exception e) {
            throw new IllegalArgumentException(FAIL_DECRYPT, e);
        }
    }

    private String decrypt(final String encryptedAccountNumber) throws Exception {
        byte[] decoded = Base64.getDecoder()
                .decode(encryptedAccountNumber);
        byte[] decrypted = decryptionCipher.doFinal(decoded);
        return new String(decrypted);
    }
}
