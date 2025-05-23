package com.social_service.util;

import com.social_service.constant.Message;
import com.social_service.repository.InvalidatedTokenRepository;
import com.social_service.service.EmailService;
import com.social_service.service.SystemLogService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DataScheduler {

    InvalidatedTokenRepository invalidatedTokenRepository;

    SystemLogService systemLogService;

    EmailService emailService;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanTokens() throws Exception {
        invalidatedTokenRepository.deleteAll();
        systemLogService.createLog("token", Message.CLEAN.getKey(), Message.CLEAN_SUCCESS.getKey());
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanLogs() throws Exception {
        systemLogService.cleanLogs();
    }

    @Scheduled(fixedRate = 30000)
    public void processEmails() throws Exception {
        emailService.processEmails();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void cleanEmails() throws Exception {
        emailService.cleanEmails();
    }
}
