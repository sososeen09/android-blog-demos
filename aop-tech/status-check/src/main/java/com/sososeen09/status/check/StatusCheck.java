package com.sososeen09.status.check;

import android.content.Context;

/**
 * @author sososeen09
 */
public interface StatusCheck {
    /**
     * 检查状态
     *
     * @return true表示检查通过，false表示检查不通过
     */
    boolean doCheck(Context context);
}
