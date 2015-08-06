package com.mfodepositorsacc.accountingmachine.utils;

/**
 * Created by berz on 14.01.15.
 */
public class Assert {

    public static void isTrue(boolean cond, String text) {
        if (!cond)
            throw new IllegalArgumentException(text);
    }

    public static void isTrue(boolean cond) {
        isTrue(cond, "Assertion Failed");
    }

    public static void isNotNull(Object obj) {
        isTrue(obj != null, "Null assertion Failed");
    }

    public static void isNotNull(Object obj, String text) {
        isTrue(obj != null, text);
    }

}
