package com.mfodepositorsacc.billing.dmodel;

import com.mfodepositorsacc.accountingmachine.dmodel.Document;

import javax.persistence.*;

/**
 * Created by berz on 17.01.15.
 */
@Entity(name = "ClientPaymentDocument")
@Table(name = "money_document")
@DiscriminatorValue("client_payment_document")
public class ClientPaymentDocument extends Document {

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public enum State {
        NEW,
        DONE
    }

    @Column(name = "key_state")
    @Enumerated(EnumType.STRING)
    protected State state;

    @Column(name = "client_id")
    protected Long clientId;

    @Column(name = "order_id")
    protected Long orderId;


    @Override
    public String toString() {
        return "{clientPaymentDocument}";
    }


}
