package com.example.microservice.controller;


import com.example.microservice.service.EmailVerificationTokenService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/verify-email")
public class VerifyEmailController {
    EmailVerificationTokenService verifyService;

    @org.springframework.beans.factory.annotation.Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

     public VerifyEmailController(EmailVerificationTokenService verifyService) {
        this.verifyService = verifyService;
    }

    @GetMapping("/confirm")
    public String verifyEmail(@RequestParam String token) {
        verifyService.verifyEmail(token);
        return """
        <html>
        <head>
            <title>Xác thực email</title>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    background-color: #f4f4f9;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    height: 100vh;
                    margin: 0;
                }
                .box {
                    background-color: #ffffff;
                    padding: 30px;
                    border-radius: 10px;
                    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
                    text-align: center;
                }
                h2 {
                    color: #4caf50;
                }
                p {
                    color: #555555;
                }
                button {
                    background-color: #4caf50;
                    color: white;
                    padding: 10px 20px;
                    border: none;
                    border-radius: 5px;
                    cursor: pointer;
                }
            </style>
        </head>
        <body>
            <div class="box">
                <h2>Xác thực email thành công</h2>
                <p>Bạn có thể quay lại website để tiếp tục đăng nhập</p>
                <button onclick="window.location.href='%s/login'">
                    Quay lại website
                </button>
            </div>
        </body>
        </html>
    """.formatted(frontendUrl);
     }
}
