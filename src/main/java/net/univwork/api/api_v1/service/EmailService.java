package net.univwork.api.api_v1.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender emailSender;

    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String senderMailAddress;

    @Async
    public void sendEmail(String receiverEmailAddress) throws MessagingException {
        String code = createVerifyCode();
        MimeMessage message = emailSender.createMimeMessage();
        message.setFrom(senderMailAddress);
        message.addRecipients(MimeMessage.RecipientType.TO, receiverEmailAddress);
        message.setSubject("[univwork.net 인증코드] " + code); // 이메일 제목
        message.setText(setContext(code), StandardCharsets.UTF_8.name().toLowerCase(), "html");
        emailSender.send(message);
    }

    private String setContext(String code) {
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process("emailTemplate", context);
    }

    private String createVerifyCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 100000부터 999999까지의 범위 무작위 정수 생성
        return String.valueOf(code);
    }
}
