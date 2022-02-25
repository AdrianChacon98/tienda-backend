package com.store.example.email;

import lombok.AllArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;



@Service
@AllArgsConstructor
public class EmailServiceImp implements EmailSender{


    private final Logger logger = LoggerFactory.getLogger(EmailServiceImp.class);

    private  JavaMailSender mailSender; // if you call the variable emailSender could it has problems

    @Async
    public void send(String to, String email){

        try{



            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,"utf-8");

            helper.setText(email,true);
            helper.setTo(to);
            helper.setSubject("Confirm your account");
            helper.setFrom("cursosprogramacion44@gmail.com");

            mailSender.send(mimeMessage);


        }catch(MessagingException e){

            logger.info(e.getMessage());

        }
    }


}
