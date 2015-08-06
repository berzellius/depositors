package com.mfodepositorsacc.dto;

import com.mfodepositorsacc.dmodel.DepositorTypeSettings;

import java.math.BigDecimal;

/**
 * Created by berz on 29.04.15.
 */
public class DepositCalculation {

    /*
    *
    * Исходные данные
     */
    public BigDecimal sum;
    public Integer length;
    public DepositorTypeSettings.DepositorFormType depositorFormType;

    public DepositCalculation(){}

    public DepositCalculation(BigDecimal sum, Integer length, DepositorTypeSettings.DepositorFormType depositorFormType){
        this.sum = sum;
        this.length = length;
        this.depositorFormType = depositorFormType;
    }

    /*
    *
    * Определяется в сервисах
    *
    * Процентная ставка
    */
    public BigDecimal percents;

    /*
    * Сумма в конце срока
     */
    public BigDecimal sumInTheEnd;

    /*
    * Заработок на вкладе
     */
    public BigDecimal profit;

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public DepositorTypeSettings.DepositorFormType getDepositorFormType() {
        return depositorFormType;
    }

    public void setDepositorFormType(DepositorTypeSettings.DepositorFormType depositorFormType) {
        this.depositorFormType = depositorFormType;
    }
}
