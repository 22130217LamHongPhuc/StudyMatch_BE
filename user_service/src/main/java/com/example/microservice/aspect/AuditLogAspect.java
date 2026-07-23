package com.example.microservice.aspect;

import com.example.microservice.repository.AuditLogRepository;
import com.example.microservice.service.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
public class AuditLogAspect {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired(required = false)
    private HttpServletRequest request;

    private final ExpressionParser parser = new SpelExpressionParser();

    @AfterReturning(pointcut = "@annotation(auditLogAnnotation)", returning = "result")
    public void logAdminAction(JoinPoint joinPoint, com.example.microservice.annotation.AuditLog auditLogAnnotation, Object result) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return;
            }

            Object principal = authentication.getPrincipal();
            if (!(principal instanceof CustomUserDetails)) {
                return;
            }

            CustomUserDetails userDetails = (CustomUserDetails) principal;
            Long adminId = userDetails.getUser().getUserId();

            // Evaluate SpEL expressions for targetId and details
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String[] parameterNames = signature.getParameterNames();
            Object[] args = joinPoint.getArgs();

            StandardEvaluationContext context = new StandardEvaluationContext();
            if (parameterNames != null) {
                for (int i = 0; i < parameterNames.length; i++) {
                    context.setVariable(parameterNames[i], args[i]);
                }
            }
            context.setVariable("result", result);

            String evaluatedTargetId = null;
            if (auditLogAnnotation.targetId() != null && !auditLogAnnotation.targetId().isEmpty()) {
                try {
                    Expression targetIdExp = parser.parseExpression(auditLogAnnotation.targetId());
                    Object val = targetIdExp.getValue(context);
                    evaluatedTargetId = val != null ? val.toString() : null;
                } catch (Exception e) {
                    evaluatedTargetId = auditLogAnnotation.targetId();
                }
            }

            String evaluatedDetails = null;
            if (auditLogAnnotation.details() != null && !auditLogAnnotation.details().isEmpty()) {
                try {
                    Expression detailsExp = parser.parseExpression(auditLogAnnotation.details());
                    Object val = detailsExp.getValue(context);
                    evaluatedDetails = val != null ? val.toString() : null;
                } catch (Exception e) {
                    evaluatedDetails = auditLogAnnotation.details();
                }
            }

            String ipAddress = null;
            if (request != null) {
                ipAddress = request.getHeader("X-Forwarded-For");
                if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
                    ipAddress = request.getRemoteAddr();
                }
            }

            com.example.microservice.entity.AuditLog logRecord = com.example.microservice.entity.AuditLog.builder()
                    .adminId(adminId)
                    .action(auditLogAnnotation.action())
                    .targetType(auditLogAnnotation.targetType())
                    .targetId(evaluatedTargetId)
                    .details(evaluatedDetails)
                    .ipAddress(ipAddress)
                    .createdAt(LocalDateTime.now())
                    .build();

            auditLogRepository.save(logRecord);

        } catch (Exception e) {
            System.err.println("Failed to write audit log: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
