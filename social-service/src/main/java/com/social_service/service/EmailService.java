package com.social_service.service;

import com.social_service.model.request.EmailRequest;

public interface EmailService {

    void saveEmail(EmailRequest request) throws Exception;

    void processEmails() throws Exception;

    void cleanEmails() throws Exception;
}
