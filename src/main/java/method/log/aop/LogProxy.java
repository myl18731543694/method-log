package method.log.aop;

import method.log.config.LogConfig;
import method.log.utils.LogUtils;
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

    @Around("@annotation(method.log.annotation.Log)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        return LogUtils.around(pjp, httpServletRequest, logConfig);
    }

}
