package com.game.aop;

import org.aopalliance.intercept.Joinpoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * Created by jennifert on 6/27/2017.
 */
@Aspect
@Component
public class IpCheckAspect {

    @Before("@annotation(com.game.annotation.IpCheck)")
    public void beforeExec(Joinpoint joinpoint) {
        System.out.println("test");
    }
}
