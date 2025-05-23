//package com.mumu.utils;
//
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//@Aspect
//@Component
//public class LoggingAspect {
//
//    @Before("execution(* com.mumu.*Controller.*(..))")
//    public void logBefore(JoinPoint joinPoint) {
//        String methodName = joinPoint.getSignature().getName();
//        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//
//        System.out.println("Method " + methodName + " is called at " + timestamp);
//    }
//}
