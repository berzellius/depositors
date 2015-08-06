package com.mfodepositorsacc.dto;

import java.math.BigDecimal;

/**
 * Created by berz on 08.07.15.
 */
public class AccountSaldoGraphElement {

    /*
    *
    * Unix timestamp даты изменения
     */
    private Long timestamp;

    private BigDecimal saldo;

    public AccountSaldoGraphElement(long time, BigDecimal saldo) {
        this.setTimestamp(time);
        this.setSaldo(saldo);
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
}
