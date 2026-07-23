package com.example.microservice.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditLog {
    String action();
    String targetType() default "";
    String targetId() default ""; // SpEL expression to dynamically extract target ID
    String details() default "";  // SpEL expression to dynamically build log details
}
