package com.mfodepositorsacc.accountingmachine.exception;

import com.mfodepositorsacc.accountingmachine.dmodel.Account;
import com.mfodepositorsacc.accountingmachine.dmodel.AccountTransaction;

import java.math.BigDecimal;

/**
 * Created by berz on 14.01.15.
 */
public class RedSaldoException extends Exception {
    String message = "В результате транзакции счету будет присвоено недопустимое сальдо.";

    public RedSaldoException() {
    }

    public RedSaldoException(String Message) {
        super(Message);
    }

    public RedSaldoException(Account acc) {
        this.message = getRedSaldoMsgByAcc(acc, null);
    }


    public RedSaldoException(Account acc, AccountTransaction trans) {
        this.message = getRedSaldoMsgByAcc(acc, trans);
    }


    String getRedSaldoMsgByAcc(Account acc, AccountTransaction trans) {
        String msg = "";
        if (trans == null) {
            msg = "В результате транзакции счету " + acc + " будет присвоено недопустимое сальдо: " + acc.getSaldo() + " у.е., или " + acc.getSaldo().divide(BigDecimal.valueOf(10000L)) + "$";
        } else {
            msg = "В результате транзакции " + trans + " счету " + acc + " будет присвоено недопустимое сальдо: " + acc.getSaldo() + " у.е., или " + acc.getSaldo().divide(BigDecimal.valueOf(10000L)) + "$";
        }
        return msg;
    }


    @Override
    public String getMessage() {
        return message;
    }
}

