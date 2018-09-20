package com.sosoeen09.activitychecker.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sosoeen09.activity.checker.Checker;
import com.sosoeen09.activitychecker.sample.check.BindPhoneChecker;
import com.sosoeen09.activitychecker.sample.check.LoginChecker;

@Checker({LoginChecker.class, BindPhoneChecker.class})
public class Activity4 extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_4);
    }
}
