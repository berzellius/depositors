package com.mfodepositorsacc.billing.dmodel;

import com.mfodepositorsacc.accountingmachine.dmodel.Document;

import javax.persistence.*;

/**
 * Created by berz on 20.06.15.
 */
@Entity(name = "DepositPayMonthlyProfitDocument")
@Table(name = "money_document")
@DiscriminatorValue("deposit_pay_in_monthly_profit_document")
public class DepositPayMonthlyProfitDocument extends Document {

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Long getDepositId() {
        return depositId;
    }

    public void setDepositId(Long depositId) {
        this.depositId = depositId;
    }

    public enum State {
        NEW,
        DONE
    }

    @Column(name = "key_state")
    @Enumerated(EnumType.STRING)
    protected State state;

    @Column(name = "deposit_id")
    protected Long depositId;


    @Override
    public String toString() {
        return "{DepositPayMonthlyProfitDocument:".concat(this.getNum()).concat("}");
    }
}
