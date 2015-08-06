package com.mfodepositorsacc.billing.dmodel;

import com.mfodepositorsacc.accountingmachine.dmodel.Document;

import javax.persistence.*;

/**
 * Created by berz on 17.01.15.
 */
@Entity(name = "DepositPayInAccountDocument")
@Table(name = "money_document")
@DiscriminatorValue("deposit_pay_in_acc_document")
public class DepositPayInAccountDocument extends Document {

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public static enum Type{
        /*
        *
        * Базовая часть вклада
         */
        BASE,
        /*
        *
        * Дополнительные взносы
         */
        ADDITIONAL
    }

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
        CANCELLED, DONE
    }

    @Column(name = "key_state")
    @Enumerated(EnumType.STRING)
    protected State state;

    @Column(name = "deposit_id")
    protected Long depositId;

    @Column(name = "type")
    @Enumerated(value = EnumType.STRING)
    protected Type type;

    @Override
    public String toString() {
        return "{DepositPayInAccountDocument:".concat(this.getNum()).concat("}");
    }
}
