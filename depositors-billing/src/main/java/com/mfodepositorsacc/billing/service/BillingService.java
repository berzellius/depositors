package com.mfodepositorsacc.billing.service;

import com.mfodepositorsacc.accountingmachine.dmodel.Document;
import com.mfodepositorsacc.accountingmachine.exception.RedSaldoException;
import com.mfodepositorsacc.billing.dmodel.DepositOutcomeDocument;
import com.mfodepositorsacc.billing.dmodel.DepositPayInAccountDocument;
import com.mfodepositorsacc.billing.exception.DocumentAlreadyProcessingException;
import com.mfodepositorsacc.billing.exception.DocumentNotFoundException;
import com.mfodepositorsacc.billing.exception.IllegalDocumentStateException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by berz on 17.01.15.
 */
@Service
public interface BillingService {

//    void doClientPaymentForOrder(Long clientId, Long orderId, BigDecimal sum) throws RedSaldoException;

    DepositPayInAccountDocument doDepositPayInAccountRequest(Long depositId, BigDecimal sum, DepositPayInAccountDocument.Type type);

    void doDepositPayInAccountConfirm(DepositPayInAccountDocument depositPayInAccountDocument) throws IllegalDocumentStateException;

    void doDepositPayInMonthlyProfit(Long depositId, BigDecimal sum, Date nextCapitalization);

    DepositOutcomeDocument doDepositOutcomeRequest(Long depositId, BigDecimal sum, DepositOutcomeDocument.Type type) throws RedSaldoException;

    void doDepositOutcomeConfirm(DepositOutcomeDocument depositOutcomeDocument) throws RedSaldoException, IllegalDocumentStateException;

    void cancelDepositOutcome(DepositOutcomeDocument depositOutcomeDocument) throws RedSaldoException, DocumentAlreadyProcessingException, IllegalDocumentStateException;

    BigDecimal getDepositSaldo(Long depositId);

    DepositPayInAccountDocument getPayInAccountDocumentByNum(String num) throws DocumentNotFoundException;

    BigDecimal getDepositSaldoLockedToOutcome(Long depositId);

    Document getDocumentByNum(String code) throws DocumentNotFoundException;

    BigDecimal getDepositAvailableSaldo(Long depositId);


//    Page<AccountHistory> getAccountHistoryByClientId(Long id, Pageable pageable);
}
