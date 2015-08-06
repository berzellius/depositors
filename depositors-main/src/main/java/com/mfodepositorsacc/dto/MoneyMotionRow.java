package com.mfodepositorsacc.dto;

import com.mfodepositorsacc.accountingmachine.dmodel.Document;
import com.mfodepositorsacc.dmodel.Deposit;
import com.mfodepositorsacc.dmodel.User;

/**
 * Created by berz on 21.06.15.
 */
public class MoneyMotionRow extends Deposit {

    public MoneyMotionRow(Deposit deposit, Document document){
        this.setDocument(document);

        this.setId(deposit.getId());
        this.setDepositor(deposit.getDepositor());
        this.setPercent(deposit.getPercent());
        this.setSum(deposit.getSum());
        this.setActivated(deposit.isActivated());
        this.setNextCapitalization(deposit.getNextCapitalization());
        this.setDtmCreate(deposit.getDtmCreate());
        this.setDtmUpdate(deposit.getDtmUpdate());
        this.setLength(deposit.getLength());
    }

    protected Document document;

    public Document getDocument(){
        return this.document;
    }

    public void setDocument(Document document){
        this.document = document;
    }


}
