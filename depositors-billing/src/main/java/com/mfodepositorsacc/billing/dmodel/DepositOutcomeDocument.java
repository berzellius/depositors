package com.mfodepositorsacc.billing.dmodel;

import com.mfodepositorsacc.accountingmachine.dmodel.Document;

import javax.persistence.*;

/**
 * Created by berz on 20.06.15.
 */
@Entity(name = "DepositOutcomeDocument")
@Table(name = "money_document")
@DiscriminatorValue("deposit_outcome_document")
public class DepositOutcomeDocument extends Document {

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public static enum Type {
        /*
        *
        * Досрочное снятие
         */
        EARLY,
        /*
        *
        * Закрытие вклада
         */
        CLOSING
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
        DONE,
        CANCELLED
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
        return "{DepositOutcomeDocument:".concat(this.getNum()).concat("}");
    }

}
