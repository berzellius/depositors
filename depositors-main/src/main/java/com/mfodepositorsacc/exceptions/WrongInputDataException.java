package com.mfodepositorsacc.exceptions;

/**
 * Created by berz on 07.11.14.
 */
public class WrongInputDataException extends Exception {

    public Reason getReason() {
        return reason;
    }

    public void setReason(Reason reason) {
        this.reason = reason;
    }

    public enum Reason{
        NAME_FIELD,
        USERNAME_FIELD,
        DUPLICATE_USERNAME,
        PASSWORD_FIELD,
        USER_ALREADY_EXISTS,
        URL_FIELD,
        DESCRIPTION_FIELD,
        CODE_FIELD,
        UNIQUE,
        PRICE_FIELD,
        COST_FIELD,
        WRONG_SUM, DEPOSIT_CALCULATION, USER_ACTIVATION_CODE, PASSWORDS_MISMATCH, DEPOSIT_FIELD, DEPOSITOR_FIELD, EMAIL_FIELD, USER_FIELD;
    }

    private Reason reason;

    public WrongInputDataException(String s){
        super(s);
    }

    public  WrongInputDataException(String s, Reason r){
        super(s);
        this.setReason(r);
    }

}
