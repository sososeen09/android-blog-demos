package com.sosoeen09.activitychecker.sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sosoeen09.activity.checker.Checker;
import com.sosoeen09.activitychecker.sample.check.LoginChecker;


public class MainActivity extends BaseActivity implements View.OnClickListener {
    TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_activity_2).setOnClickListener(this);
        findViewById(R.id.btn_activity_3).setOnClickListener(this);
        findViewById(R.id.btn_activity_4).setOnClickListener(this);
        findViewById(R.id.btn_reset).setOnClickListener(this);
        findViewById(R.id.btn_test_fragment).setOnClickListener(this);

        tvStatus = findViewById(R.id.tv_show_status);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setStatus();
    }

    private void setStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("登录: ");
        sb.append(StatusHolder.sHasLogin);
        sb.append("\n");

        sb.append("绑定手机号: ");
        sb.append(StatusHolder.sHasBindPhone);

        tvStatus.setText(sb);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_activity_2:
                toActivity2();
                break;
            case R.id.btn_activity_3:
                toActivity3();
                break;
            case R.id.btn_activity_4:
                toActivity4();
                break;
            case R.id.btn_reset:
                StatusHolder.reset();
                setStatus();
                break;
            case R.id.btn_test_fragment:
                toTestFragmentActivity();
                break;
            default:
                break;
        }
    }

    //跳转到Activity2，需要登录
    private void toActivity2() {
        startActivity(new Intent(this, Activity2.class));
    }

    private void toActivity3() {
        startActivity(new Intent(this, Activity3.class));
    }

    //跳转到Activity2，需要登录和绑定手机号
    private void toActivity4() {
        startActivity(new Intent(this, Activity4.class));
    }

    private void toTestFragmentActivity() {
        startActivity(new Intent(this, TestFragmentActivity.class));
    }
}
