package com.example.insecurecollab.util;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class InsecurePasswordUtil {

    public String hash(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hashed = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : hashed) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean matches(String raw, String existingHash) {
        return hash(raw).equals(existingHash);
    }
}
