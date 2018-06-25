package com.sososeen09.aop.kotlin

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.sososeen09.aop.kotlin.check.BindPhoneStatusCheck
import com.sososeen09.aop.kotlin.check.LoginStatusCheck
import com.sososeen09.status.check.Check

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.btn_activity_2).setOnClickListener(this)
        findViewById<View>(R.id.btn_activity_3).setOnClickListener(this)
        findViewById<View>(R.id.btn_activity_4).setOnClickListener(this)

        tvStatus = findViewById(R.id.tv_show_status)
    }

    override fun onResume() {
        super.onResume()
        val sb = StringBuilder()
        sb.append("登录: ")
        sb.append(StatusHolder.sHasLogin)
        sb.append("\n")

        sb.append("绑定手机号: ")
        sb.append(StatusHolder.sHasBindPhone)

        tvStatus.text = sb
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_activity_2 -> toActivity2()

            R.id.btn_activity_3 -> toActivity3()
            R.id.btn_activity_4 -> toActivity4()
            else -> {
            }
        }
    }

    @Check(LoginStatusCheck::class)
    private fun toActivity2() {
        startActivity(Intent(this, Activity2::class.java))
    }

    @Check(BindPhoneStatusCheck::class)
    private fun toActivity3() {
        startActivity(Intent(this, Activity3::class.java))
    }

    @Check(LoginStatusCheck::class, BindPhoneStatusCheck::class)
    private fun toActivity4() {
        startActivity(Intent(this, Activity4::class.java))
    }

}
