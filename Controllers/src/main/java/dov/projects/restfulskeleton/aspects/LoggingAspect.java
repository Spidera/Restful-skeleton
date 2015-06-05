package dov.projects.restfulskeleton.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic logging aspect for controllers
 */
@Aspect
public class LoggingAspect {
    private Logger log = LoggerFactory.getLogger(getClass());

    @After("execution(public * dov.projects.restfulskeleton.controllers.*..*(..))")
    public void log(JoinPoint point) {
        log.info(point.getSignature().getName() + " executing...");
    }

}
