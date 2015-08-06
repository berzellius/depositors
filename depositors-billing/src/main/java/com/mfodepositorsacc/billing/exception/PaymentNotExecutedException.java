package com.mfodepositorsacc.billing.exception;

/**
 * Created by berz on 17.01.15.
 */
public class PaymentNotExecutedException extends Exception {

    public Reason getReason() {
        return reason;
    }

    public void setReason(Reason reason) {
        this.reason = reason;
    }

    public enum Reason{
        NOT_ENOUGH_MONEY
    }

    private Reason reason;


    public PaymentNotExecutedException(String msg){
        super(msg);
    }

    public PaymentNotExecutedException(String msg, Reason reason){
        super(msg);
        this.setReason(reason);
    }

}
