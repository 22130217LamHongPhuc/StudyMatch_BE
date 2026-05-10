package com.example.microservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class JavaMail {

    private final String fromEmail;
    private final String password;

    public JavaMail(
            @Value("${mail.email}") String fromEmail,
            @Value("${mail.password}") String password
    ) {
        this.fromEmail = fromEmail;
        this.password = password;
    }

    public  void sendEmail(String toEmail, String subject, String type,String link) {


        System.out.println("Preparing to from email  " + fromEmail);

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");


        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });


        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(toEmail)
            );
            message.setSubject(subject);
            String htmlContent = "";
            if(type.equals("verify-email")){
                htmlContent = buildEmailTemplate(toEmail, link);
            }else if(type.equals("reset-password")){
                htmlContent = buildResetPasswordTemplate(toEmail, link);
            }

            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);

            System.out.println("Email đã được gửi thành công!");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Gửi email thất bại!");
        }
    }

    private String buildEmailTemplate(String userName, String link) {
        return """
        <div style="font-family: Arial, sans-serif; background-color: #f4f6f8; padding: 40px;">
            <div style="max-width: 600px; margin: auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.1);">
                
                <!-- Header -->
                <div style="background: linear-gradient(90deg, #4CAF50, #2E7D32); padding: 20px; text-align: center;">
                    <h1 style="color: white; margin: 0;">StudyMatch</h1>
                </div>

                <!-- Body -->
                <div style="padding: 30px; text-align: center;">
                    <h2 style="color: #333;">Hi bạn </h2>
                    
                    <p style="color: #555; font-size: 16px;">
                        Cảm ơn bạn đã đăng ký tài khoản tại <b>StudyMatch</b>!  
                        Hãy xác thực email để bắt đầu trải nghiệm.
                    </p>

                    <!-- Button -->
                    <a href="%s"
                       style="display: inline-block; margin-top: 20px; padding: 14px 28px; 
                              font-size: 16px; color: white; background-color: #4CAF50; 
                              border-radius: 6px; text-decoration: none;">
                        Xác thực Email
                    </a>

                    <p style="margin-top: 20px; font-size: 14px; color: #999;">
                        Nếu nút không hoạt động, copy link dưới:
                    </p>

                    <p style="word-break: break-all; font-size: 13px; color: #777;">
                        %s
                    </p>
                </div>

                <!-- Footer -->
                <div style="background: #f1f1f1; padding: 15px; text-align: center; font-size: 12px; color: #888;">
                    © 2026 StudyMatch. All rights reserved.
                </div>
            </div>
        </div>
        """.formatted(link, link);
    }


    private String buildResetPasswordTemplate(String userName, String link) {
        return """
    <div style="font-family: Arial, sans-serif; background-color: #f4f6f8; padding: 40px;">
        <div style="max-width: 600px; margin: auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.1);">
            
            <!-- Header -->
            <div style="background: linear-gradient(90deg, #FF9800, #F57C00); padding: 20px; text-align: center;">
                <h1 style="color: white; margin: 0;">StudyMatch</h1>
            </div>

            <!-- Body -->
            <div style="padding: 30px; text-align: center;">
                <h2 style="color: #333;">Xin chào </h2>
                
                <p style="color: #555; font-size: 16px;">
                    Bạn vừa yêu cầu thiết lập lại mật khẩu cho tài khoản <b>StudyMatch</b>.
                </p>

                <p style="color: #555; font-size: 16px;">
                    Nhấn vào nút bên dưới để đặt mật khẩu mới:
                </p>

                <!-- Button -->
                <a href="%s"
                   style="display: inline-block; margin-top: 20px; padding: 14px 28px; 
                          font-size: 16px; color: white; background-color: #FF9800; 
                          border-radius: 6px; text-decoration: none;">
                    Đặt lại mật khẩu
                </a>

                <p style="margin-top: 20px; font-size: 14px; color: #999;">
                    Link này sẽ hết hạn sau <b>15 phút</b>.
                </p>

                <p style="margin-top: 10px; font-size: 14px; color: #999;">
                    Nếu bạn không yêu cầu, hãy bỏ qua email này.
                </p>

                <p style="margin-top: 20px; font-size: 14px; color: #999;">
                    Nếu nút không hoạt động, copy link dưới:
                </p>

                <p style="word-break: break-all; font-size: 13px; color: #777;">
                    %s
                </p>
            </div>

            <!-- Footer -->
            <div style="background: #f1f1f1; padding: 15px; text-align: center; font-size: 12px; color: #888;">
                © 2026 StudyMatch. All rights reserved.
            </div>
        </div>
    </div>
    """.formatted(link, link);
    }
}
