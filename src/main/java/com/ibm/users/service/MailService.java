package com.ibm.users.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailService {
	
	@Value("${spring.mail.maillink}")
	private String maillink;
	  
	// private static Logger logger = LoggerFactory.getLogger(MailService.class);
	
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private JavaMailSender javaMailSender;

    /**
     * a发送简单文本文件 for test
     */
    public void sendSimpleEmail(){
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("liker007@163.com");
            message.setTo("xuexp@cn.ibm.com");
            message.setSubject("hello");
            message.setText("helloha");
            message.setCc("liker007@163.com");
            mailSender.send(message);

        }catch (Exception e){
        	e.printStackTrace();
            System.out.println("发送简单文本文件-发生异常"+e);
        }
    }

    /**
     * a发送html文本
     * @param
     * @throws MessagingException 
     */
    @Async
    public void sendHTMLMail(String email, String username) throws MessagingException{
    
//        try {
    	MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("liker007@163.com");
            messageHelper.setTo(email);
            messageHelper.setCc("liker007@163.com");
            messageHelper.setSubject("Welcome to SMC system");
            messageHelper.setText("<a href='"+ maillink + username + "'>please click here to confirm your sign up!</a>", true);
            mailSender.send(mimeMessage);
            System.out.println("发送html文本文件-成功");
//        }catch (Exception e){
//        	e.printStackTrace();
//        	System.out.println("发送html文本文件-发生异常");
//        	logger.error("html email send failed!", e.getMessage());
//        }
    }
    
    /**
     * a发送new pw
     */
    public void sendNewPasswordEmail(String email, String newpassword){
//        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("liker007@163.com");
            message.setTo(email);
            message.setSubject("Your New Password to Login SMC System");
            message.setText("Your New Password >>>> "+ newpassword);
            message.setCc("liker007@163.com");
            mailSender.send(message);

//        }catch (Exception e){
//        	e.printStackTrace();
//            System.out.println("发送简单文本文件-发生异常"+e.getMessage());
//        }
    }
    
    
}
