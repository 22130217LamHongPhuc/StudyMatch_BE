package com.example.microservice.controller;


import com.example.microservice.dto.respone.ApiResponse;
import com.example.microservice.enums.StatusCode;
import com.example.microservice.service.EmailVerificationTokenService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/verify-email")
public class VerifyEmailController {
    EmailVerificationTokenService verifyService;

     public VerifyEmailController(EmailVerificationTokenService verifyService) {
        this.verifyService = verifyService;
    }

    @GetMapping("/confirm")
    public String verifyEmail(@RequestParam String token) {
        verifyService.verifyEmail(token);
        return """
        <html>
        <head>
            <title>Verify Email</title>
            <style>
                body {
                    font-family: Arial;
                    text-align: center;
                    margin-top: 100px;
                }
                .box {
                    border: 1px solid #ddd;
                    padding: 20px;
                    display: inline-block;
                    border-radius: 10px;
                    box-shadow: 0 0 10px rgba(0,0,0,0.1);
                }
                button {
                    padding: 10px 20px;
                    background-color: #4CAF50;
                    color: white;
                    border: none;
                    border-radius: 5px;
                    cursor: pointer;
                }
            </style>
        </head>
        <body>
            <div class="box">
                <h2> Email verified successfully</h2>
                <p>You can now go back to the website</p>
                <button onclick="window.location.href='http://localhost:3000/login'">
                    Go to Website
                </button>
            </div>
        </body>
        </html>
    """;
     }
}
