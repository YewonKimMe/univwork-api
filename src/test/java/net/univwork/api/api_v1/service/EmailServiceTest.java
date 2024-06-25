package net.univwork.api.api_v1.service;

import jakarta.mail.MessagingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @DisplayName("Verify Email Send Test")
    @Test
    void sendEmail() {
        emailService.sendVerifyEmail("kyw0428@gnu.ac.kr", UUID.randomUUID().toString());
    }
}