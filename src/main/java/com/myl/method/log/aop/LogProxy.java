package com.myl.method.log.aop;

import com.myl.method.log.config.LogConfig;
import com.myl.method.log.utils.LogUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@AllArgsConstructor
public class LogProxy {

    private final HttpServletRequest httpServletRequest;

    private final LogConfig logConfig;

    @Around("@annotation(com.myl.method.log.annotation.Log)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        return LogUtils.around(pjp, httpServletRequest, logConfig);
    }

}
