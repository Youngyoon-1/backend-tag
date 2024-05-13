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
    private static Cipher cipher;

    @BeforeAll
    static void setUp() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        converter = new AccountNumberConverter("aaaaaaaaaaaaaaaaaaaaaa1234567890");
        cipher = Cipher.getInstance("AES");
        final byte[] decodedKey = Base64.getDecoder().decode("aaaaaaaaaaaaaaaaaaaaaa1234567890");
        final SecretKeySpec keySpec = new SecretKeySpec(decodedKey, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
    }

    @Test
    void 암호화를_한다() throws IllegalBlockSizeException, BadPaddingException {
        // when
        final String encryptedData = converter.convertToDatabaseColumn("test");

        // then
        // test 값을 암호화를 한다
        byte[] encrypted = cipher.doFinal("test".getBytes());
        final String expectation = Base64.getEncoder().encodeToString(encrypted);
        Assertions.assertThat(encryptedData).isEqualTo(expectation);
    }

    @Test
    void 복호화를_한다() throws IllegalBlockSizeException, BadPaddingException {
        // given
        // test 값을 암호화를 한다
        final byte[] encryptedByte = cipher.doFinal("test".getBytes());
        final String encryptedStr = Base64.getEncoder().encodeToString(encryptedByte);

        // when
        final String decryptedData = converter.convertToEntityAttribute(encryptedStr);

        // then
        Assertions.assertThat(decryptedData).isEqualTo("test");
    }
}
