package com.sososeen09.status.check;

import android.content.Context;
import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sososeen09
 */
@Aspect
public class StatusCheckAspect {
    private static final String TAG = "StatusCheckAspect";
    private Map<Class<? extends StatusCheck>, StatusCheck> cache = new HashMap<>();

    //定义切面的规则
    //1.就在原来应用中哪些注释的地方放到当前切面进行处理
    //execution(注解名   注解用的地方)
    //方法名自己定义
    @Pointcut("execution(@com.sososeen09.status.check.Check * *(..))")
    public void checkStatus() {

    }

    //2.对进入切面的内容如何处理
    //advice
    //@Before()  在切入点之前运行
    //@After()   在切入点之后运行
    //@Around()  在切入点前后都运行
    //方法名自己定义
    @Around("checkStatus()")
    public void aroundJointPoint(final ProceedingJoinPoint joinPoint) throws Throwable {
        //初始化context
        Context context = null;

        final Object object = joinPoint.getThis();
        if (object instanceof Context) {
            context = (Context) object;
        } else if (object instanceof android.support.v4.app.Fragment) {
            context = ((android.support.v4.app.Fragment) object).getActivity();
        } else if (object instanceof android.app.Fragment) {
            context = ((android.app.Fragment) object).getActivity();
        } else {
        }

        if (context == null) {
            Log.e(TAG, "aroundJonitPoint checkStatus error ");
            joinPoint.proceed();
            return;
        }

        //获取方法信息
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Class<? extends StatusCheck>[] value = methodSignature.getMethod().getAnnotation(Check.class).value();

        boolean checkSuccess = true;
        for (Class<? extends StatusCheck> aClass : value) {
            StatusCheck statusCheck = cache.get(aClass);
            if (statusCheck == null) {
                statusCheck = aClass.newInstance();
                cache.put(aClass, statusCheck);
            }

            if (!statusCheck.doCheck(context)) {
                checkSuccess = false;
                break;
            }
        }

        if (checkSuccess) {
            joinPoint.proceed();
        }
    }
}
