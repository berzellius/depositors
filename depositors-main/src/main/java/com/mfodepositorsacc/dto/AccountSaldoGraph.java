package com.mfodepositorsacc.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by berz on 08.07.15.
 */
public class AccountSaldoGraph {

    private List<AccountSaldoGraphElement> values;

    private List<AccountSaldoGraphDescriptionElement> descriptions;

    private BigDecimal minSaldo;

    private BigDecimal maxSaldo;

    BigDecimal max;

    public AccountSaldoGraph(List<AccountSaldoGraphElement> accountSaldoGraphElements, List<AccountSaldoGraphDescriptionElement> accountSaldoGraphDescriptionElements, BigDecimal minSaldo, BigDecimal maxSaldo) {
        this.setValues(accountSaldoGraphElements);
        this.setDescriptions(accountSaldoGraphDescriptionElements);
        this.setMaxSaldo(maxSaldo);
        this.setMinSaldo(minSaldo);
    }

    public void setMinSaldo(BigDecimal minSaldo){
        this.minSaldo = minSaldo;
    }

    public BigDecimal getMinSaldo(){
        return this.minSaldo;
    }

    public void setMaxSaldo(BigDecimal maxSaldo){
        this.maxSaldo = maxSaldo;
    }

    public BigDecimal getMaxSaldo(){
        return this.maxSaldo;
    }

    public List<AccountSaldoGraphElement> getValues() {
        return values;
    }

    public void setValues(List<AccountSaldoGraphElement> values) {
        this.values = values;
    }

    public List<AccountSaldoGraphDescriptionElement> getDescriptions(){
        return descriptions;
    }

    public void setDescriptions(List<AccountSaldoGraphDescriptionElement> descriptions){
        this.descriptions = descriptions;
    }
}
