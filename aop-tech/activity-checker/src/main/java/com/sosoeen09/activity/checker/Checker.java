package com.sosoeen09.activity.checker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于注解Activity，如果该Activity上面有该注解就要获取对应的ActivityChecker，检查是否满足条件跳转到该页面
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Checker {
    Class<? extends ActivityChecker>[] value();
}
