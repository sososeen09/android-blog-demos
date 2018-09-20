package com.sosoeen09.activity.checker;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;

import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * 用于管理Activity的启动
 */
public class ActivityCheckerManager {
    public static final String FLAG_FROM_ACTIVITY_CHECKER = "FLAG_FROM_ACTIVITY_CHECKER";

    private static final class CheckerManagerHolder {
        private static final ActivityCheckerManager INSTANCE = new ActivityCheckerManager();
    }

    public static ActivityCheckerManager getInstance() {
        return CheckerManagerHolder.INSTANCE;
    }

    private SoftReference<Map> mCacheMapHolder = null;

    private ActivityCheckerManager() {
    }

    public boolean startActivityForResult(Activity activity, Intent intent, int requestCode) {
        return startActivityForResult(activity,new ActivityLauncher(activity),intent,requestCode);
    }

    public boolean startActivityForResult(Fragment fragment, Intent intent, int requestCode) {
        return startActivityForResult(fragment.getActivity(),new ActivityLauncher(fragment),intent,requestCode);
    }

    public boolean startActivityForResult(android.support.v4.app.Fragment v4Fragment, Intent intent, int requestCode) {
        return startActivityForResult(v4Fragment.getContext(),new ActivityLauncher(v4Fragment),intent,requestCode);
    }

    private boolean startActivityForResult(Context context, ActivityLauncher launcher, Intent intent, int requestCode) {
        if (context == null || intent == null || intent.getBooleanExtra(FLAG_FROM_ACTIVITY_CHECKER,false)) {
            return false;
        }
        intent.putExtra(FLAG_FROM_ACTIVITY_CHECKER,true);

        Class<? extends Activity> activityClass = null;


        if (intent.getComponent() != null
            // && context.getPackageName().equals(intent.getComponent().getPackageName())
                ) {
            try {
                activityClass = (Class<? extends Activity>) Class.forName(intent.getComponent().getClassName());
            } catch (Throwable e) {

            }
        }
        if (activityClass == null) {
            return false;
        }

        Checker anno = activityClass.getAnnotation(Checker.class);
        if (anno == null) {
            return false;
        }
        Class<? extends ActivityChecker>[] classes = anno.value();
        if (classes == null || classes.length == 0) {
            return false;
        }

        if (new HashSet<Class<?>>(Arrays.asList(classes)).size() < classes.length) {
            throw new IllegalStateException("Can not have duplicate ActivityChecker! " + activityClass.getName());
        }

        boolean res = false;
        for (Class<? extends ActivityChecker> clazz : classes) {
            ActivityChecker activityChecker = getActivityCheckerByType(clazz);
            if (activityChecker != null) {
                if (activityChecker.intercept(context,launcher,intent,requestCode)) {
                    //如果有一个拦截的，就不再判断后面的了
                    res = true;
                    break;
                }
            }
        }
        return res;
    }

    //通过类型获取缓存的实例
    private ActivityChecker getActivityCheckerByType(Class<? extends ActivityChecker> clazz) {
        if (mCacheMapHolder == null || mCacheMapHolder.get() == null) {
            mCacheMapHolder = new SoftReference(new HashMap<>());
        }
        Map<Class<? extends ActivityChecker>,Object> map = mCacheMapHolder.get();
        ActivityChecker activityChecker = (ActivityChecker) map.get(clazz);
        if (activityChecker == null) {
            try {
                activityChecker = clazz.newInstance();
            } catch (Exception e) {

            }
            map.put(clazz,activityChecker);
        }
        return activityChecker;
    }
}
