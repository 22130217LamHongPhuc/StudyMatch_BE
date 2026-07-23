package com.group_service.aspect;

import com.group_service.annotation.AuditLog;
import com.group_service.clients.UserClient;
import com.group_service.dto.AuditLogSaveRequest;
import com.group_service.dto.TokenValidateResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final UserClient userClient;
    private final ExpressionParser expressionParser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    @AfterReturning(value = "@annotation(auditLog)", returning = "result")
    public void logAdminAction(JoinPoint joinPoint, AuditLog auditLog, Object result) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }

        HttpServletRequest request = attributes.getRequest();
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return; // No logged-in admin context, skip audit logging
        }

        Long adminId = null;
        try {
            TokenValidateResponse validateResponse = userClient.validateToken(authHeader);
            if (validateResponse != null && validateResponse.isValid()) {
                adminId = validateResponse.getUserId();
            }
        } catch (Exception e) {
            System.err.println("AuditLogAspect: Failed to validate token via user-service: " + e.getMessage());
        }

        if (adminId == null) {
            return; // Could not authenticate admin, skip logging to avoid DB constraint failure
        }

        // Evaluate SpEL expressions
        String action = auditLog.action();
        String targetType = auditLog.targetType();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        String[] paramNames = discoverer.getParameterNames(method);

        EvaluationContext context = new StandardEvaluationContext();
        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
        }
        context.setVariable("result", result);

        String targetId = parseSpel(auditLog.targetId(), context);
        String details = parseSpel(auditLog.details(), context);

        // Get IP Address
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        } else {
            // Take the first IP if forwarded through multiple proxies
            int index = ipAddress.indexOf(",");
            if (index != -1) {
                ipAddress = ipAddress.substring(0, index);
            }
        }

        // Send audit log to user_service
        try {
            AuditLogSaveRequest logRequest = AuditLogSaveRequest.builder()
                    .adminId(adminId)
                    .action(action)
                    .targetId(targetId)
                    .targetType(targetType)
                    .details(details)
                    .ipAddress(ipAddress)
                    .build();
            userClient.saveAuditLogInternal(logRequest);
        } catch (Exception e) {
            System.err.println("AuditLogAspect: Failed to save audit log: " + e.getMessage());
        }
    }

    private String parseSpel(String spelExpr, EvaluationContext context) {
        if (spelExpr == null || spelExpr.trim().isEmpty()) {
            return "";
        }
        try {
            Expression expression = expressionParser.parseExpression(spelExpr);
            Object value = expression.getValue(context);
            return value != null ? value.toString() : "";
        } catch (Exception e) {
            // Fallback to raw string if SpEL parsing fails
            return spelExpr;
        }
    }
}
