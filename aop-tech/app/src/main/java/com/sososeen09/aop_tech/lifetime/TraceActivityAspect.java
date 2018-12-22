package com.sososeen09.aop_tech.lifetime;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * 检测activity生命周期耗时
 */
@Aspect
public class TraceActivityAspect {

  public static final String TAG = "TraceActivityAspect";

  @Pointcut("execution(* android.app.Activity.on**(..))")
  public void activityOnXXX() {
  }

  @Around("activityOnXXX()")
  public Object activityOnXXXAdvice(ProceedingJoinPoint proceedingJoinPoint) {
    Object result = null;
    try {
      Activity activity = (Activity) proceedingJoinPoint.getTarget();
      long startTime = System.currentTimeMillis();
      result = proceedingJoinPoint.proceed();
      String activityName = activity.getClass().getCanonicalName();

      Signature signature = proceedingJoinPoint.getSignature();
      String sign = "";
      String methodName = "";
      if (signature != null) {
        sign = signature.toString();
        methodName = signature.getName();
      }

      long duration = System.currentTimeMillis() - startTime;
      if (!TextUtils.isEmpty(activityName) && !TextUtils.isEmpty(sign) && sign
          .contains(activityName)) {
        Log.e(TAG,
            String.format("Activity：%s, %s方法执行了，用时%d ms", activityName, methodName, duration));
      }
    } catch (Exception e) {
      e.printStackTrace();
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
    return result;
  }
}
