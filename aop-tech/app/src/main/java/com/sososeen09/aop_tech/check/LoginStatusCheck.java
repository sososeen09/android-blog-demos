package com.sososeen09.aop_tech.check;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.sososeen09.aop_tech.LoginActivity;
import com.sososeen09.aop_tech.StatusTest;
import com.sososeen09.status.check.StatusCheck;


/**
 * @author sososeen09
 */
public class LoginStatusCheck implements StatusCheck {
    @Override
    public boolean doCheck(Context context) {
        if (!StatusTest.sHasLogin) {
            onCheckNotPassed(context);
        }
        return StatusTest.sHasLogin;
    }

    private String getTitle() {
        return "登录检查";
    }

    private String getNotPassedMsg() {
        return "未登录，请先登录之后再继续后续操作";
    }

    private void onConfirm(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
    }

    private void onCheckNotPassed(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(getTitle())
                .setMessage(getNotPassedMsg())
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onConfirm(context);
                    }
                }).setNegativeButton("取消", null);

        builder.show();
    }
}
