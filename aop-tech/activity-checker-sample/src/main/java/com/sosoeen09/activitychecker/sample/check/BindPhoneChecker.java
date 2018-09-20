package com.sosoeen09.activitychecker.sample.check;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.sosoeen09.activity.checker.ActivityChecker;
import com.sosoeen09.activity.checker.ActivityLauncher;
import com.sosoeen09.activitychecker.sample.BindPhoneActivity;
import com.sosoeen09.activitychecker.sample.StatusHolder;

public class BindPhoneChecker implements ActivityChecker {
    @Override
    public boolean intercept(Context context, ActivityLauncher launcher, Intent intent, int requestCode) {
        if (!StatusHolder.sHasBindPhone) {
//            onCheckNotPassed(context);
            Intent newIntent = new Intent(context, BindPhoneActivity.class);
            launcher.startActivity(newIntent);
            return true;
        }
        return !StatusHolder.sHasBindPhone;
    }

    private String getTitle() {
        return "手机号检查";
    }

    private String getNotPassedMsg() {
        return "未绑定手机号，请先绑定好手机号";
    }

    private void onConfirm(Context context) {
        context.startActivity(new Intent(context, BindPhoneActivity.class));
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
