package com.social_service.service.impl;

import com.social_service.constant.EmailStatus;
import com.social_service.constant.Message;
import com.social_service.model.entity.EmailEntity;
import com.social_service.model.entity.UserEntity;
import com.social_service.model.request.EmailRequest;
import com.social_service.repository.EmailRepository;
import com.social_service.repository.UserRepository;
import com.social_service.service.EmailService;
import com.social_service.service.SystemLogService;
import com.social_service.util.Translator;
import io.micrometer.common.util.StringUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j(topic = "EMAIL-SERVICE")
public class EmailServiceImpl implements EmailService {

    EmailRepository emailRepository;

    UserRepository userRepository;

    SystemLogService systemLogService;

    JavaMailSender javaMailSender;

    SpringTemplateEngine templateEngine;

    public boolean sendEmailSync(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        if (StringUtils.isBlank(to) || StringUtils.isBlank(subject) || StringUtils.isBlank(content)) {
            log.error("Email validation failed - to: {}", to);
            return false;
        }

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper message =
                    new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content, isHtml);

            javaMailSender.send(mimeMessage);
            return true;

        } catch (MailException | MessagingException e) {
            log.error("Error sending email to {}: {}", to, e.getMessage(), e);
            return false;
        }
    }

    @Async
    public CompletableFuture<Boolean> sendEmailFromTemplateSync(EmailRequest request) throws Exception {
        if (StringUtils.isBlank(request.getRecipient())) {
            throw new BadRequestException(Translator.toLocale(Message.EMAIL_INVALID.getKey(), null));
        }

        try {
            Context context = new Context();
            context.setVariable("otp", request.getOtp());
            context.setVariable("subject", request.getSubject());
            context.setVariable("recipient", request.getRecipient());

            String content = templateEngine.process(request.getTemplate(), context);

            boolean emailSent =
                    sendEmailSync(request.getRecipient(), request.getSubject(), content, false, true);

            if (!emailSent) {
                log.error("Failed to send templated email to {}", request.getRecipient());
            }

            return CompletableFuture.completedFuture(emailSent);
        } catch (TemplateInputException e) {
            log.error("Template '{}' not found: {}", request.getTemplate(), e.getMessage(), e);
            throw new BadRequestException(Translator.toLocale(Message.ERROR.getKey(), null));
        }
    }

    @Override
    public void saveEmail(EmailRequest request) throws Exception {
        log.info("Email save request: {}", request);

        EmailEntity email = EmailEntity.builder()
                .recipient(request.getRecipient())
                .subject(request.getSubject())
                .template(request.getTemplate())
                .status(EmailStatus.PENDING)
                .duration(request.getDuration())
                .type(request.getType())
                .build();

        emailRepository.save(email);
    }

    @Override
    public void processEmails() throws Exception {
        log.info("Email process request");

        List<EmailEntity> emails =
                emailRepository.findByStatusAndDurationIsBefore(EmailStatus.PENDING, Instant.now()).orElse(null);

        if (emails == null || emails.isEmpty()) {
            log.info("No pending emails to process.");
            return;
        }

        log.info("Emails processed: {}", emails.size());

        List<EmailEntity> toUpdate = new ArrayList<>();
        List<EmailEntity> toDelete = new ArrayList<>();

        for (EmailEntity email : emails) {
            try {
                UserEntity user = userRepository.findByEmail(email.getRecipient()).orElse(null);

                if (user != null && user.getOtp() != null) {

                    EmailRequest emailRequest = EmailRequest.builder()
                            .recipient(email.getRecipient())
                            .subject(email.getSubject())
                            .otp(user.getOtp())
                            .template(email.getTemplate())
                            .build();

                    boolean sent = sendEmailFromTemplateSync(emailRequest).get();
                    email.setStatus(sent ? EmailStatus.SENT : EmailStatus.PENDING);
                    toUpdate.add(email);
                } else {
                    email.setStatus(EmailStatus.FAILED);
                    toDelete.add(email);
                }
            } catch (Exception ex) {
                email.setStatus(EmailStatus.FAILED);
                toUpdate.add(email);
            }
        }

        if (!toUpdate.isEmpty()) {
            emailRepository.saveAll(toUpdate);
        }

        if (!toDelete.isEmpty()) {
            emailRepository.deleteAll(toDelete);
        }
    }

    @Override
    public void cleanEmails() throws Exception {
        List<EmailEntity> emails =
                emailRepository.findByStatusOrDurationIsAfter(EmailStatus.SENT, Instant.now()).orElse(null);
        if (emails == null || emails.isEmpty()) {
            return;
        }

        emailRepository.deleteAll(emails);
        systemLogService.createLog("email", Message.CLEAN.getKey(), Message.CLEAN_SUCCESS.getKey());
    }
}
