package com.sososeen09.aop_tech;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import com.sososeen09.aop_tech.aspect.TimeSpend;

public class BindPhoneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone);

        findViewById(R.id.btn_bind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptBind();
            }
        });
    }

    @TimeSpend("绑定手机号")
    private void attemptBind() {
        StatusHolder.sHasBindPhone = true;
        Toast.makeText(BindPhoneActivity.this, "绑定手机号成功", Toast.LENGTH_SHORT).show();
        finish();
    }
}
