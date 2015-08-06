package com.mfodepositorsacc.billing;

import com.mfodepositorsacc.accountingmachine.dmodel.AccountHistory;
import com.mfodepositorsacc.accountingmachine.dmodel.Document;
import com.mfodepositorsacc.accountingmachine.exception.RedSaldoException;
import com.mfodepositorsacc.billing.dmodel.DepositOutcomeDocument;
import com.mfodepositorsacc.billing.dmodel.DepositPayInAccountDocument;
import com.mfodepositorsacc.billing.exception.DocumentAlreadyProcessingException;
import com.mfodepositorsacc.billing.exception.DocumentNotFoundException;
import com.mfodepositorsacc.billing.exception.DuplicatePaymentException;
import com.mfodepositorsacc.billing.exception.IllegalDocumentStateException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by berz on 30.12.14.
 */
@Service
public interface BillingMainContract {

    DepositPayInAccountDocument doDepositBasePayInAccountRequest(Long depositId, BigDecimal sum) throws DuplicatePaymentException;

    DepositPayInAccountDocument doDepositAdditionalPayInAccountRequest(Long depositId, BigDecimal sum);

    void doDepositPayInAccountConfirm(DepositPayInAccountDocument depositPayInAccountDocument) throws IllegalDocumentStateException;

    void doDepositPayInMonthlyProfit(Long depositId, BigDecimal sum, Date nextCapitalization);

    DepositOutcomeDocument doDepositEarlyOutcomeRequest(Long depositId, BigDecimal sum) throws RedSaldoException;

    DepositOutcomeDocument doDepositClosingOutcomeRequest(Long depositId, BigDecimal sum) throws DuplicatePaymentException, RedSaldoException;

    void doDepositOutcomeConfirm(DepositOutcomeDocument depositOutcomeDocument) throws RedSaldoException, IllegalDocumentStateException;

    BigDecimal getDepositSaldo(Long depositId);

    DepositPayInAccountDocument getPayInAccountDocumentByNum(String num) throws DocumentNotFoundException;

    DepositOutcomeDocument getDepositOutcomeDocumentByNum(String docNum) throws DocumentNotFoundException;

    Long countNewBasePayInRequests();

    Long countNewAdditionalPayInRequests();

    Long countNewEarlyOutcomeRequests();

    Page<DepositPayInAccountDocument> newBasePayInRequests(Pageable pageable);

    Page<DepositPayInAccountDocument> newAdditionalPayInRequests(Pageable pageable);

    Page<DepositOutcomeDocument> newEarlyOutcomeRequests(Pageable pageable);

    DepositPayInAccountDocument getBasePayInAccountDocumentByDepositId(Long depositId);

    List<Document> getCapitalizationLog(Long id);

    List<Document> getPayInAccountLog(Long id);

    List<Document> getPayInAccountRequestsUndoneLog(Long id);

    void cancelRequestBeforeMoneyTransferByDocNumber(String code) throws DocumentNotFoundException, DocumentAlreadyProcessingException, RedSaldoException, IllegalDocumentStateException;

    List<Document> getOutcomeLog(Long depositId);

    List<Document> getOutcomeUndoneLog(Long depositId);

    BigDecimal getDepositLockedToOutcomeSaldo(Long depositId);

    List<AccountHistory> getDepositAccHistory(Long depositId, Date after);

    List<AccountHistory> getDepositAvailableAccHistory(Long id, Date after);

    BigDecimal getDepositAvailableSaldo(Long depositId);
}
