package com.example.microservice.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MailService {
    @Autowired
    JavaMail javaMail;



    public void sendMailTo(String to, String subject, String type, String link) {
        javaMail.sendEmail(to, subject, type,link);
    }

}
