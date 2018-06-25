package com.sososeen09.aop.kotlin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast

class BindPhoneActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bind_phone)

        findViewById<View>(R.id.btn_bind).setOnClickListener {
            StatusHolder.sHasBindPhone = true
            Toast.makeText(this@BindPhoneActivity, "绑定手机号成功", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
