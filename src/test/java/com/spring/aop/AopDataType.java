package com.spring.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * Created by lenovo on 2017/7/6.
 */
@Aspect
@Component
public class AopDataType {
 /*   @Pointcut("execution(* com.spring.aop.*.set*(..))")
    //  (execution(* com.xxx.*.controller.*.*(..))
    public void postCut() {
        System.out.println("sdsdsdsd");
    }

    @Pointcut("@annotation(com.spring.aop.Money)")
    public void annotionMoney() {

    }*/

    //可以直接获取注解中的元数据
    //此种写法必须是类级别，接口级别无法扫描到
    @Before("@annotation(money)")
    public void before(JoinPoint joinPoint, Money money) {
        System.out.println("##before money===" + money.name());

    }

    // @Before("postCut()")
    public void around(JoinPoint joinPoint) throws Throwable {
//获取属性
        String methodName = joinPoint.getSignature().getName();
        StringBuilder stringBuilder = new StringBuilder(methodName);
        stringBuilder.delete(0, 3);//删除set、get
        stringBuilder.setCharAt(0, Character.toLowerCase(stringBuilder.charAt(0)));
        System.out.println("fname==" + stringBuilder.toString());
        Field field = joinPoint.getTarget().getClass().getDeclaredField(stringBuilder.toString());
        field.setAccessible(true);
        Money m = field.getAnnotation(Money.class);
        if (m != null) {
            long mony = (long) joinPoint.getArgs()[0];
            System.out.println("##mony==" + mony);
        }
        System.out.println("##after method name::" + stringBuilder);
        System.out.println(joinPoint.getThis() + "target:" + joinPoint.getTarget().getClass().getFields());
    }

    //@Around("postCut()")

    @Around("@annotation(money)")
    public Object processed(ProceedingJoinPoint joinPoint, Money money) throws Throwable {
        System.out.println("##@ComponentScan--" + money.name());
        String methodName = joinPoint.getSignature().getName();
        StringBuilder stringBuilder = new StringBuilder(methodName);
        stringBuilder.delete(0, 3);//删除set、get
        stringBuilder.setCharAt(0, Character.toLowerCase(stringBuilder.charAt(0)));
        System.out.println("fname==" + stringBuilder.toString());
        Field field = joinPoint.getTarget().getClass().getDeclaredField(stringBuilder.toString());
        field.setAccessible(true);
        Money m = field.getAnnotation(Money.class);
        long mony = 0L;
        if (m != null) {
            mony = ((long) joinPoint.getArgs()[0]) * 100;
        }
        return joinPoint.proceed(new Object[]{mony});
    }
}
