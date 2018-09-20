package com.sosoeen09.activitychecker.sample;

/**
 * @author sososeen09
 */
public class StatusHolder {
    public static boolean sHasLogin = false;
    public static boolean sHasBindPhone = false;

    public static void reset() {
        sHasLogin = false;
        sHasBindPhone = false;
    }
}
