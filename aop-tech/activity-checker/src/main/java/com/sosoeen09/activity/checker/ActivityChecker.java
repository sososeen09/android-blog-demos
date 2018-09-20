package com.sosoeen09.activity.checker;

import android.content.Context;
import android.content.Intent;

public interface ActivityChecker {
    /**
     * 检查是否需要拦截
     * @param context
     * @param launcher
     * @param intent
     * @param requestCode
     * @return false 不拦截，true 拦截
     */
    boolean intercept(Context context, ActivityLauncher launcher, Intent intent, int requestCode);
}
