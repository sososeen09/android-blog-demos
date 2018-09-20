package com.sosoeen09.activitychecker.sample.check;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.sosoeen09.activity.checker.ActivityChecker;
import com.sosoeen09.activity.checker.ActivityLauncher;
import com.sosoeen09.activitychecker.sample.LoginActivity;
import com.sosoeen09.activitychecker.sample.StatusHolder;

public class LoginChecker implements ActivityChecker {
    @Override
    public boolean intercept(Context context, ActivityLauncher launcher, Intent intent, int requestCode) {
        if (!StatusHolder.sHasLogin) {
            onCheckNotPassed(context);
        }
        return !StatusHolder.sHasLogin;
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
