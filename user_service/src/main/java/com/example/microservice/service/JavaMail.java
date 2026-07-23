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
            }else if(type.equals("admin-invitation")){
                htmlContent = buildAdminInvitationTemplate(toEmail, link);
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

    public void sendSessionReminderEmail(String toEmail, String fullName, String sessionTitle, String startTime, String groupName) {
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
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("📚 Nhắc lịch học: " + sessionTitle);
            String htmlContent = buildSessionReminderTemplate(fullName, sessionTitle, startTime, groupName);
            message.setContent(htmlContent, "text/html; charset=utf-8");
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String buildSessionReminderTemplate(String fullName, String sessionTitle, String startTime, String groupName) {
        return """
        <div style="font-family: Arial, sans-serif; background-color: #f4f6f8; padding: 40px;">
            <div style="max-width: 600px; margin: auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.1);">
                
                <div style="background: linear-gradient(90deg, #2196F3, #1565C0); padding: 20px; text-align: center;">
                    <h1 style="color: white; margin: 0;">StudyMatch</h1>
                </div>

                <div style="padding: 30px; text-align: center;">
                    <h2 style="color: #333;">Xin chào %s 👋</h2>
                    
                    <p style="color: #555; font-size: 16px;">
                        Buổi học của bạn sắp bắt đầu!
                    </p>

                    <div style="background: #e3f2fd; border-radius: 8px; padding: 20px; margin: 20px 0; text-align: left;">
                        <p style="margin: 8px 0; color: #333; font-size: 15px;">
                            📖 <b>Buổi học:</b> %s
                        </p>
                        <p style="margin: 8px 0; color: #333; font-size: 15px;">
                            👥 <b>Nhóm:</b> %s
                        </p>
                        <p style="margin: 8px 0; color: #333; font-size: 15px;">
                            🕐 <b>Bắt đầu lúc:</b> %s
                        </p>
                    </div>

                    <p style="color: #555; font-size: 14px;">
                        Hãy chuẩn bị và tham gia đúng giờ nhé!
                    </p>
                </div>

                <div style="background: #f1f1f1; padding: 15px; text-align: center; font-size: 12px; color: #888;">
                    © 2026 StudyMatch. All rights reserved.
                </div>
            </div>
        </div>
        """.formatted(fullName, sessionTitle, groupName, startTime);
    }

    public void sendGroupLockEmail(String toEmail, String groupName, String status) {
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
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Thông báo trạng thái nhóm học: " + groupName);

            String statusText = "bị khóa";
            if ("DELETED".equalsIgnoreCase(status)) {
                statusText = "xóa";
            }

            String content = String.format(
                "<div style=\"font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f7f5f0; padding: 40px 20px;\">" +
                "    <div style=\"max-width: 540px; margin: 0 auto; background-color: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 8px 30px rgba(0,0,0,0.06); border: 1px solid #e5e5e0;\">" +
                "        <div style=\"background-color: #0f172a; padding: 24px; text-align: center;\">" +
                "            <h1 style=\"color: #ffffff; margin: 0; font-size: 22px; font-weight: 700; letter-spacing: 0.5px;\">StudyMatch</h1>" +
                "            <p style=\"color: #94a3b8; margin: 4px 0 0 0; font-size: 12px; text-transform: uppercase; font-weight: 600; letter-spacing: 1px;\">Hệ thống hỗ trợ học tập</p>" +
                "        </div>" +
                "        <div style=\"padding: 32px 24px;\">" +
                "            <div style=\"text-align: center; margin-bottom: 24px;\">" +
                "                <h2 style=\"color: #0f172a; margin: 0; font-size: 20px; font-weight: 700;\">Thông báo trạng thái nhóm</h2>" +
                "            </div>" +
                "            <p style=\"color: #334155; font-size: 14px; line-height: 1.6; margin: 0 0 16px 0;\">Xin chào <b>Trưởng nhóm</b>,</p>" +
                "            <p style=\"color: #334155; font-size: 14px; line-height: 1.6; margin: 0 0 24px 0;\">" +
                "                Chúng tôi rất tiếc phải thông báo rằng nhóm học <b>%s</b> của bạn đã <b>%s</b> trên hệ thống StudyMatch." +
                "            </p>" +
                "            <div style=\"background-color: #f8fafc; border-left: 4px solid #dc2626; border-radius: 6px; padding: 16px; margin-bottom: 24px;\">" +
                "                <p style=\"margin: 0; color: #475569; font-size: 13px; line-height: 1.5; font-weight: 500;\">" +
                "                    <b>Lý do:</b> Nhóm học của bạn đã được xác định là vi phạm các chính sách cộng đồng và điều khoản dịch vụ của StudyMatch." +
                "                </p>" +
                "            </div>" +
                "            <p style=\"color: #475569; font-size: 13px; line-height: 1.6; margin: 0 0 24px 0;\">" +
                "                Mọi thắc mắc hoặc yêu cầu làm rõ, vui lòng liên hệ với Ban quản trị StudyMatch bằng cách phản hồi lại email này để được hỗ trợ giải quyết nhanh nhất." +
                "            </p>" +
                "            <div style=\"border-top: 1px solid #f1f5f9; padding-top: 20px;\">" +
                "                <p style=" +
                "\"margin: 0; color: #64748b; font-size: 12px;\">Trân trọng,</p>" +
                "                <p style=\"margin: 4px 0 0 0; color: #0f172a; font-size: 13px; font-weight: 600;\">Ban quản trị StudyMatch</p>" +
                "            </div>" +
                "        </div>" +
                "        <div style=\"background-color: #f8fafc; border-top: 1px solid #f1f5f9; padding: 16px 24px; text-align: center;\">" +
                "            <p style=\"margin: 0; color: #94a3b8; font-size: 11px;\">Email này được gửi tự động từ hệ thống StudyMatch. Vui lòng không gửi thư rác.</p>" +
                "        </div>" +
                "    </div>" +
                "</div>",
                groupName,
                statusText
            );

            message.setContent(content, "text/html; charset=utf-8");
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String buildAdminInvitationTemplate(String userName, String link) {
        return """
        <div style="font-family: Arial, sans-serif; background-color: #f4f6f8; padding: 40px;">
            <div style="max-width: 600px; margin: auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.1);">
                <div style="background: linear-gradient(90deg, #4F46E5, #3730A3); padding: 20px; text-align: center;">
                    <h1 style="color: white; margin: 0;">StudyMatch Admin Portal</h1>
                </div>
                <div style="padding: 30px; text-align: center;">
                    <h2 style="color: #333;">Lời mời tham gia ban quản trị</h2>
                    <p style="color: #555; font-size: 16px;">
                        Bạn đã được mời trở thành Quản trị viên (Admin) của hệ thống <b>StudyMatch</b>.
                    </p>
                    <p style="color: #555; font-size: 16px;">
                        Vui lòng nhấp vào nút dưới đây để thiết lập mật khẩu và kích hoạt tài khoản của bạn:
                    </p>
                    <a href="%s"
                       style="display: inline-block; margin-top: 20px; padding: 14px 28px; 
                              font-size: 16px; color: white; background-color: #4F46E5; 
                              border-radius: 6px; text-decoration: none; font-weight: bold;">
                        Thiết lập tài khoản
                    </a>
                    <p style="margin-top: 20px; font-size: 14px; color: #999;">
                        Liên kết này có hiệu lực trong vòng 24 giờ.
                    </p>
                    <p style="margin-top: 20px; font-size: 14px; color: #999;">
                        Nếu nút không hoạt động, vui lòng sao chép liên kết dưới đây:
                    </p>
                    <p style="word-break: break-all; font-size: 13px; color: #777;">
                        %s
                    </p>
                </div>
                <div style="background: #f1f1f1; padding: 15px; text-align: center; font-size: 12px; color: #888;">
                    © 2026 StudyMatch. All rights reserved.
                </div>
            </div>
        </div>
        """.formatted(link, link);
    }
}

