package com.sososeen09.aop.kotlin.check

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import com.sososeen09.aop.kotlin.BindPhoneActivity
import com.sososeen09.aop.kotlin.StatusHolder
import com.sososeen09.status.check.StatusCheck

/**
 * @author sososeen09
 */
class BindPhoneStatusCheck : StatusCheck {
    override fun doCheck(context: Context): Boolean {
        if (!StatusHolder.sHasBindPhone) {
            onCheckNotPassed(context)
        }
        return StatusHolder.sHasBindPhone
    }

    private fun getTitle(): String {
        return "手机号检查"
    }

    private fun getNotPassedMsg(): String {
        return "未绑定手机号，请先绑定好手机号"
    }

    private fun onConfirm(context: Context) {
        context.startActivity(Intent(context, BindPhoneActivity::class.java))
    }

    private fun onCheckNotPassed(context: Context) {
        val builder = AlertDialog.Builder(context)
                .setTitle(getTitle())
                .setMessage(getNotPassedMsg())
                .setPositiveButton("确定") { dialog, which -> onConfirm(context) }.setNegativeButton("取消", null)

        builder.show()
    }
}