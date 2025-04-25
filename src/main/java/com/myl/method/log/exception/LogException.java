package com.myl.method.log.exception;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;

import java.io.Serial;
import java.util.UUID;

@Getter
@Slf4j
public class LogException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 5835477792286223391L;

    private final String uid;

    private final String requestURI;

    private final String methodName;

    private final transient Object[] args;

    public LogException(ProceedingJoinPoint pjp, Exception e, @Nullable HttpServletRequest request) {
        super(e);
        uid = buildUid(e);
        requestURI = buildRequestURI(e, request);
        methodName = pjp.getSignature().toString();
        args = pjp.getArgs();
    }

    public Throwable getRootCause() {
        var currentCause = getCause();
        while (currentCause instanceof LogException) {
            currentCause = currentCause.getCause();
        }
        return currentCause;
    }

    public void printLog() {
        var cause = getCause();
        var printArgs = buildPrintArgs();
        if (cause instanceof LogException) {
            log.error("""
                异常信息打印如下，追踪UID查询完整信息
                UID【{}】
                方法【{}】
                入参【{}】
                """, uid, methodName, printArgs);
        } else {
            log.error("""
                异常信息打印如下，追踪UID查询完整信息
                UID【%s】
                请求地址【%s】
                发生异常【%s】
                方法【%s】
                入参【%s】
                栈信息如下
                """.formatted(uid, requestURI, cause.toString(), methodName, printArgs), cause);
        }
    }

    private String buildUid(Exception e) {
        if (e instanceof LogException logException) {
            return logException.getUid();
        }
        return UUID.randomUUID().toString();
    }

    private String buildRequestURI(Exception e, HttpServletRequest request) {
        if (e instanceof LogException logException) {
            return logException.getRequestURI();
        }
        try {
            return request == null ? "不是http请求" : request.getRequestURI();
        } catch (Exception httpException) {
            return "获取失败，可能不是http请求";
        }
    }

    private Object buildPrintArgs() {
        try {
            return JSON.toJSONString(args);
        } catch (Exception e) {
            log.error("入参转换json失败，方法【%s】".formatted(methodName), e);
            return args;
        }
    }

}
