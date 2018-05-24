package mvc.token.filter;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

//@Aspect
@Component
public class SignatureAspect {

    @Pointcut("execution(public * mvc.token.controller..*.*(..))")
    public void controllerSignature() {

    }

    @Before("controllerSignature()")
    public void  doBefore(JoinPoint joinPoint) {

        try {
            System.out.println("=========== do before ==========");
            throw new RuntimeException("do before exception");
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @AfterThrowing(pointcut = "controllerSignature()", throwing = "e")
    public void ThrowHandle(Exception e) {
        System.out.println("========= do throwHandle =====");
        e.printStackTrace();
    }
}
