package com.tag.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

@Converter
public class AccountNumberConverter implements AttributeConverter<String, String> {

    private final TextEncryptor encryptor;

    public AccountNumberConverter(@Value("${encryption.password}") final String password,
                                  @Value("${encryption.salt}") final String salt) {
        this.encryptor = Encryptors.text(password, salt);
    }

    @Override
    public String convertToDatabaseColumn(final String attribute) {
        if (attribute == null) {
            return null;
        }
        if (attribute.equals("")) {
            return "";
        }
        return encryptor.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(final String dbData) {
        if (dbData == null) {
            return null;
        }
        if (dbData.equals("")) {
            return "";
        }
        return encryptor.decrypt(dbData);
    }
}
