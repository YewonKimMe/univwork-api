package net.univwork.api.api_v1.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
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

    private final Environment environment;

    @Async
    public void sendVerifyEmail(String receiverEmailAddress, String authToken) {

        String host = environment.getProperty("email.verify.path.host");
        String url = environment.getProperty("email.verify.path.url");

        StringBuilder sb = new StringBuilder();
        sb.append(host);
        sb.append(url);
        sb.append("?");
        sb.append("authToken=");
        sb.append(authToken);
        String verifyLink = sb.toString();

        log.debug("verifyLink={}", verifyLink);
        MimeMessage message = emailSender.createMimeMessage();
        try {
            message.setFrom(senderMailAddress);
            message.addRecipients(MimeMessage.RecipientType.TO, receiverEmailAddress);
            message.setSubject("[univwork 메일 인증]"); // 이메일 제목
            message.setText(setContext(verifyLink), StandardCharsets.UTF_8.name().toLowerCase(), "html");
        } catch (MessagingException e) {
            throw new RuntimeException(e.getMessage());
        }
        emailSender.send(message);
    }

    private String setContext(String link) {
        Context context = new Context();
        context.setVariable("link", link);
        context.setVariable("subLink", link);
        return templateEngine.process("emailTemplate", context);
    }

    public String createVerifyCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 100000부터 999999까지의 범위 무작위 정수 생성
        return String.valueOf(code);
    }
}
