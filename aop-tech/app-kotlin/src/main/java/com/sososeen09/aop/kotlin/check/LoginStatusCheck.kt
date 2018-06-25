package com.sososeen09.aop.kotlin.check

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import com.sososeen09.aop.kotlin.LoginActivity
import com.sososeen09.aop.kotlin.StatusHolder
import com.sososeen09.status.check.StatusCheck


/**
 * @author sososeen09
 */
class LoginStatusCheck : StatusCheck {
    override fun doCheck(context: Context): Boolean {
        if (!StatusHolder.sHasLogin) {
            onCheckNotPassed(context)
        }
        return StatusHolder.sHasLogin
    }

    private val title: String
        get() = "登录检查"

    private val notPassedMsg: String
        get() = "未登录，请先登录之后再继续后续操作"

    private fun onConfirm(context: Context) {
        context.startActivity(Intent(context, LoginActivity::class.java))
    }

    private fun onCheckNotPassed(context: Context) {
        val builder = AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(notPassedMsg)
                .setPositiveButton("确定") { dialog, which -> onConfirm(context) }.setNegativeButton("取消", null)

        builder.show()
    }
}
