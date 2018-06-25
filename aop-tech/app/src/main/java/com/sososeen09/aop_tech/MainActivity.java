package com.sososeen09.aop_tech;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.sososeen09.aop_tech.check.BindPhoneStatusCheck;
import com.sososeen09.aop_tech.check.LoginStatusCheck;
import com.sososeen09.status.check.Check;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_activity_2).setOnClickListener(this);
        findViewById(R.id.btn_activity_3).setOnClickListener(this);
        findViewById(R.id.btn_activity_4).setOnClickListener(this);

        tvStatus = findViewById(R.id.tv_show_status);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StringBuilder sb = new StringBuilder();
        sb.append("登录: ");
        sb.append(StatusTest.sHasLogin);
        sb.append("\n");

        sb.append("绑定手机号: ");
        sb.append(StatusTest.sHasBindPhone);

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

            default:
                break;
        }
    }


    @Check(LoginStatusCheck.class)
    private void toActivity2() {
        startActivity(new Intent(this, Activity2.class));
    }

    @Check({BindPhoneStatusCheck.class})
    private void toActivity3() {
        startActivity(new Intent(this, Activity3.class));
    }

    @Check({LoginStatusCheck.class, BindPhoneStatusCheck.class})
    private void toActivity4() {
        startActivity(new Intent(this, Activity4.class));
    }
}
