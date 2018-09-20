package com.sosoeen09.activitychecker.sample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.accessibility.AccessibilityManager;

import com.sosoeen09.activity.checker.ActivityCheckerManager;

public class BaseActivity extends AppCompatActivity {
    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        boolean intercept = ActivityCheckerManager.getInstance().startActivityForResult(this, intent, requestCode);
        if (!intercept) {
            super.startActivityForResult(intent, requestCode);
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        boolean intercept = ActivityCheckerManager.getInstance().startActivityForResult(this, intent, requestCode);
        if (!intercept) {
            super.startActivityForResult(intent, requestCode, options);
        }
    }
}


