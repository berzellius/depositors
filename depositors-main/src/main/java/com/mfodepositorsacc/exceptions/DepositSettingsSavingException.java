package com.mfodepositorsacc.exceptions;

/**
 * Created by berz on 26.05.15.
 */
public class DepositSettingsSavingException extends Exception {

    public Reason getReason() {
        return reason;
    }

    public void setReason(Reason reason) {
        this.reason = reason;
    }

    public static enum Reason{
        EMPTY_FIELDS, ZERO_VALUES, DUPLICATE, SUM_OUT_OF_RANGE, LENGTH_OUT_OF_RANGE
    }

    private Reason reason;

    public DepositSettingsSavingException(String msg){
        super(msg);
    }

    public DepositSettingsSavingException(String msg, Reason reason){
        super(msg);
        this.setReason(reason);
    }

}
