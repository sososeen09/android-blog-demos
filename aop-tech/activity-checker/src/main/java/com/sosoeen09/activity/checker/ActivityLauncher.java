package com.sosoeen09.activity.checker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * 用于包装启动Activity的调用者，在这里只有Activity、Fragment、android.app.Fragment（不推荐使用，尽量用v4包下的）
 */
public class ActivityLauncher {
    private Activity activity;
    private Fragment v4Fragment;
    private android.app.Fragment fragment;

    public ActivityLauncher(Activity activity) {
        this.activity = activity;
    }

    public ActivityLauncher(Fragment v4Fragment) {
        this.v4Fragment = v4Fragment;
    }

    public ActivityLauncher(android.app.Fragment fragment) {
        this.fragment = fragment;
    }

    public void startActivity(Class<? extends Activity> clazz) {
        startActivity(new Intent(getContext(),clazz));
    }

    public void startActivity(Intent intent) {
        startActivityForResult(intent,-1);
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        if (intent != null) {
            intent.putExtra(ActivityCheckerManager.FLAG_FROM_ACTIVITY_CHECKER,true);
        }
        if (activity != null) {
            activity.startActivityForResult(intent,requestCode);
        }
        else if (v4Fragment != null) {
            v4Fragment.startActivityForResult(intent,requestCode);
        }
        else if (fragment != null) {
            fragment.startActivityForResult(intent,requestCode);
        }
        else {
            throw new IllegalArgumentException("if (activity == null && v4Fragment == null && fragment == null) { throw exception } ");
        }
    }

    public Context getContext() {
        if (activity != null) {
            return activity;
        }
        else if (v4Fragment != null) {
            return v4Fragment.getContext();
        }
        else if (fragment != null) {
            return fragment.getActivity();
        }
        else {
            throw new IllegalArgumentException("if (activity == null && v4Fragment == null && fragment == null) { throw exception } ");
        }
    }
}
