package com.myl.method.log.config;

import com.myl.method.log.utils.LogUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LogAopConfig {

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private LogConfig logConfig;

    @Pointcut("execution(* com.myl.method.log.controller.*.*(..))")
    public void controller() {
    }

    @Pointcut("execution(* com.myl.method.log.service.*.*(..))")
    public void service() {
    }

    @Around("controller() || service()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        return LogUtils.around(pjp, httpServletRequest, logConfig);
    }

}
