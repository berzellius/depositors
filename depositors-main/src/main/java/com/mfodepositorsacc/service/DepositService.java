package com.mfodepositorsacc.service;

import com.mfodepositorsacc.accountingmachine.dmodel.Document;
import com.mfodepositorsacc.accountingmachine.exception.RedSaldoException;
import com.mfodepositorsacc.billing.dmodel.DepositOutcomeDocument;
import com.mfodepositorsacc.billing.dmodel.DepositPayInAccountDocument;
import com.mfodepositorsacc.billing.exception.DocumentAlreadyProcessingException;
import com.mfodepositorsacc.billing.exception.DocumentNotFoundException;
import com.mfodepositorsacc.billing.exception.IllegalDocumentStateException;
import com.mfodepositorsacc.dmodel.Deposit;
import com.mfodepositorsacc.exceptions.DepositCapitalizationIllegalStateException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * Created by berz on 21.06.15.
 */
@Service
public interface DepositService {
    HashMap<String, Object> mainContractPdfPlaceholders(Deposit deposit);

    void capitalize(Deposit deposit) throws DepositCapitalizationIllegalStateException;

    void dropCapitalizationDate(Deposit deposit);

    HashMap<String, List<Document>> getMoneyMotionLogs(Deposit deposit);

    DepositPayInAccountDocument requestAdditionalMoneyPayIn(Deposit deposit, BigDecimal sum);

    HashMap<String, List<Document>> getMoneyMotionRequestsLogs(Deposit deposit);

    void cancelRequestToPayInByDocNumber(String code) throws DocumentNotFoundException, DocumentAlreadyProcessingException, RedSaldoException, IllegalDocumentStateException;

    DepositOutcomeDocument requestEarlyOutcome(Deposit deposit, BigDecimal sum) throws RedSaldoException;

    void cancelRequestToOutcomeByDocNumber(String code) throws DocumentAlreadyProcessingException, DocumentNotFoundException, RedSaldoException, IllegalDocumentStateException;

    void updatePercent(Deposit deposit, BigDecimal percent);
}
