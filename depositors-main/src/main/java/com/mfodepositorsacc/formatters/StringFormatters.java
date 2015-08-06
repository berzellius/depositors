package com.mfodepositorsacc.formatters;

/**
 * Created by berz on 19.05.15.
 */
public class StringFormatters {
    public static String formatPhone(String phone){
        if(phone.length() != 10) return phone;

        return "8(".concat(phone.substring(0, 3)).concat(") ").concat(phone.substring(3, 6)).concat("-").concat(phone.substring(6));
    }

    public static String formatPassportNum(String passportNum){
        if(passportNum.length() != 10) return passportNum;

        return passportNum.substring(0,4).concat(" ").concat(passportNum.substring(4));
    }

    public  static String formatDepartmentCode(String code){
        if(code.length() != 6) return code;

        return code.substring(0,3).concat("-").concat(code.substring(3));
    }
}
