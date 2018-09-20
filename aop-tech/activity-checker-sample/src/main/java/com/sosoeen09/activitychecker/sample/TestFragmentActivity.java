package com.sosoeen09.activitychecker.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TestFragmentActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_fragment);
        getSupportFragmentManager().beginTransaction().add(R.id.fl_container, new TestFragment()).commit();
    }
}
