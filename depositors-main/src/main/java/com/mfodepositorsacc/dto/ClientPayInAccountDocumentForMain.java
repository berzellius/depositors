package com.mfodepositorsacc.dto;

import com.mfodepositorsacc.billing.dmodel.DepositPayInAccountDocument;

/**
 * Created by berz on 19.01.15.
 */
public class ClientPayInAccountDocumentForMain extends DepositPayInAccountDocument {

    public ClientPayInAccountDocumentForMain(DepositPayInAccountDocument clientPayInAccountDocument){
        this.setDepositId(clientPayInAccountDocument.getDepositId());
        this.setId(clientPayInAccountDocument.getId());
        this.setDtAcc(clientPayInAccountDocument.getDtAcc());
        this.setState(clientPayInAccountDocument.getState());
        this.setDtmUpdate(clientPayInAccountDocument.getDtmUpdate());
        this.setDtmCreate(clientPayInAccountDocument.getDtmCreate());
        this.setNum(clientPayInAccountDocument.getNum());
        this.setSum(clientPayInAccountDocument.getSum());
        this.setMemo(clientPayInAccountDocument.getMemo());
    }

}
