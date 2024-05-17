package com.tag.domain;

import com.tag.domain.member.AccountNumberConverter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class AccountNumberConverterTest {

    private static AccountNumberConverter converter;
    private static Cipher encryptionCipher;

    @BeforeAll
    static void setUp() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        converter = new AccountNumberConverter("aaaaaaaaaaaaaaaaaaaaaa1234567890");
        encryptionCipher = Cipher.getInstance("AES");
        final byte[] decodedKey = Base64.getDecoder().decode("aaaaaaaaaaaaaaaaaaaaaa1234567890");
        final SecretKeySpec keySpec = new SecretKeySpec(decodedKey, "AES");
        encryptionCipher.init(Cipher.ENCRYPT_MODE, keySpec);
    }

    @Test
    void 암호화를_한다() {
        // when
        final String encryptedData = converter.convertToDatabaseColumn("1234567890");

        // then
        Assertions.assertThat(encryptedData).isNotNull()
                .isNotEqualTo("1234567890");
    }

    @Test
    void 복호화를_한다() throws IllegalBlockSizeException, BadPaddingException {
        // given
        final byte[] encryptedByte = encryptionCipher.doFinal("1234567890".getBytes());
        final String encryptedString = Base64.getEncoder().encodeToString(encryptedByte);

        // when
        final String decryptedData = converter.convertToEntityAttribute(encryptedString);

        // then
        Assertions.assertThat(decryptedData).isEqualTo("1234567890");
    }
}
