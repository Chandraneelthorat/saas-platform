package com.saas.platform.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    @Value("${sendgrid.api-key}")
    private String apiKey;

    @Value("${sendgrid.from-email}")
    private String fromEmail;

    @Value("${sendgrid.from-name}")
    private String fromName;

    public void sendWelcomeEmail(String toEmail, String userName, String tenantName) {
        Email from = new Email(fromEmail, fromName);
        Email to = new Email(toEmail);

        String subject = "Welcome to " + tenantName + "! 🎉";

        String body = "Hi " + userName + ",\n\n"
                + "Welcome to " + tenantName + " on SaaS Platform!\n\n"
                + "Your account has been created successfully.\n"
                + "You can now log in and start managing your projects and tasks.\n\n"
                + "Happy building!\n"
                + "The SaaS Platform Team";

        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();

        try {

            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println("Email sent! Status: " + response.getStatusCode());
            System.out.println("Response body: " + response.getBody());        }  catch (IOException e) {
            System.err.println("Email failed: " + e.getMessage());
            e.printStackTrace();
        }

    }
}