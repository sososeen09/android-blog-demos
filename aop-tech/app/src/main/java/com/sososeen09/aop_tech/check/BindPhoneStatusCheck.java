package com.sososeen09.aop_tech.check;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.sososeen09.aop_tech.BindPhoneActivity;
import com.sososeen09.aop_tech.StatusHolder;
import com.sososeen09.status.check.StatusCheck;

/**
 * @author sososeen09
 */
public class BindPhoneStatusCheck implements StatusCheck {
    @Override
    public boolean doCheck(Context context) {
        if (!StatusHolder.sHasBindPhone) {
            onCheckNotPassed(context);
        }
        return StatusHolder.sHasBindPhone;
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
