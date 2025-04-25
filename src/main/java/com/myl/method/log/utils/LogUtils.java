package com.myl.method.log.utils;

import com.myl.method.log.config.LogConfig;
import com.myl.method.log.exception.LogException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;

public class LogUtils {

    private LogUtils() {
    }

    public static Object around(
        ProceedingJoinPoint pjp,
        HttpServletRequest httpServletRequest,
        LogConfig logConfig
    ) throws Throwable {
        try {
            return pjp.proceed();
        } catch (Exception e) {
            var ignoreExceptions = logConfig.getIgnoreExceptions();
            if (ignoreExceptions.contains(e.getClass().getName())) {
                throw e;
            }
            var logException = new LogException(pjp, e, httpServletRequest);
            logException.printLog();
            throw logException;
        }
    }

}
