package com.tsmc.cloudnative.attendancesystemapi;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


import static org.junit.jupiter.api.Assertions.*;


class PasswordEncoderTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void testPasswordHashAndMatch() {

        String rawPassword = "123";

        // hash
        String hashedPassword = passwordEncoder.encode(rawPassword);

        System.out.println("Hashed password: " + hashedPassword);

        assertNotNull(hashedPassword, "hashedPassword不應該為null");
        assertNotEquals(rawPassword, hashedPassword, "hashedPassword不應該與原密碼相同");

        assertTrue(passwordEncoder.matches(rawPassword, hashedPassword), "密碼應該匹配成功");
    }

}
