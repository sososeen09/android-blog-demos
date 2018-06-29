package com.sososeen09.aop_tech.aspect;

import android.util.Log;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.sql.Timestamp;

/**
 * 用于管理方法执行时间
 */
@Aspect
public class MethodSpendTimeAspect {
    private static final String TAG = "MethodSpendTimeAspect";

    @Pointcut("execution(@com.sososeen09.aop_tech.aspect.TimeSpend * *(..))")
    public void methodTime() {

    }

    @Around("methodTime()")
    public Object weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();
        String funName = methodSignature.getMethod().getAnnotation(TimeSpend.class).value();

        //统计时间
        long begin = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - begin;

        Log.e(TAG, String.format("功能：%s,%s类的%s方法执行了，用时%d ms", funName, className, methodName, duration));

        return result;
    }
}
