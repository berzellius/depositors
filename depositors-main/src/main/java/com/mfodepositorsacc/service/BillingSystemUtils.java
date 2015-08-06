package com.mfodepositorsacc.service;

import com.mfodepositorsacc.accountingmachine.dmodel.Document;
import com.mfodepositorsacc.accountingmachine.exception.RedSaldoException;
import com.mfodepositorsacc.billing.dmodel.DepositOutcomeDocument;
import com.mfodepositorsacc.billing.dmodel.DepositPayInAccountDocument;
import com.mfodepositorsacc.billing.exception.DocumentAlreadyProcessingException;
import com.mfodepositorsacc.billing.exception.DocumentNotFoundException;
import com.mfodepositorsacc.billing.exception.DuplicatePaymentException;
import com.mfodepositorsacc.billing.exception.IllegalDocumentStateException;
import com.mfodepositorsacc.dmodel.Deposit;
import com.mfodepositorsacc.dto.AccountSaldoGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by berz on 19.01.15.
 */
public interface BillingSystemUtils {

/*    Page<ClientPayInAccountDocumentForMain> getClientPayInDocsForMainByBillingDocs(Page<DepositPayInAccountDocument> clientPayInAccountDocuments);

    Page<ClientPayInAccountDocumentForMain> getClientPayInDocsForMainPage(Pageable pageable);*/

    DepositPayInAccountDocument doDepositBasePayInAccountRequest(Long depositId, BigDecimal sum) throws DuplicatePaymentException;

    DepositPayInAccountDocument doDepositAdditionalPayInAccountRequest(Long depositId, BigDecimal sum);

    void doDepositPayInAccountConfirm(DepositPayInAccountDocument depositPayInAccountDocument) throws IllegalDocumentStateException;

    void doDepositPayInMonthlyProfit(Long depositId, BigDecimal sum, Date nextCapitalization);

    DepositOutcomeDocument doDepositEarlyOutcomeRequest(Long depositId, BigDecimal sum) throws RedSaldoException;

    DepositOutcomeDocument doDepositClosingOutcomeRequest(Long depositId, BigDecimal sum) throws DuplicatePaymentException, RedSaldoException;

    void doDepositOutcomeConfirm(DepositOutcomeDocument depositOutcomeDocument) throws RedSaldoException, IllegalDocumentStateException;

    BigDecimal getDepositSaldo(Long depositId);

    BigDecimal getDepositAvailableSaldo(Long depositId);

    BigDecimal getDepositLockedToOutcomeSaldo(Long depositId);

    DepositPayInAccountDocument getPayInAccountDocumentByNum(String num) throws DocumentNotFoundException;

    Long countNewBasePayInRequests();

    Long countNewAdditionalPayInRequests();

    Long countNewEarlyOutcomeRequests();

    Page<DepositPayInAccountDocument> newBasePayInRequests(Pageable pageable);

    Page<DepositPayInAccountDocument> newAdditionalPayInRequests(Pageable pageable);

    Page<DepositOutcomeDocument> newEarlyOutcomeRequests(Pageable pageable);

    void doDepositPayInAccountConfirmByDocNumber(String docNum) throws DocumentNotFoundException, IllegalDocumentStateException;

    DepositPayInAccountDocument getBasePayInAccountDocumentByDepositId(Long id);

    List<Document> getCapitalizationLog(Deposit deposit);

    List<Document> getPayInAccountLog(Deposit deposit);

    List<Document> getPayInAccountRequestsUndoneLog(Deposit deposit);

    void cancelRequestBeforeMoneyTransferByDocNumber(String code) throws DocumentNotFoundException, DocumentAlreadyProcessingException, RedSaldoException, IllegalDocumentStateException;

    List<Document> getOutcomeLog(Deposit deposit);

    List<Document> getOutcomeUndoneLog(Deposit deposit);

    void doDepositOutcomeConfirmByDocNumber(String docNum) throws DocumentNotFoundException, RedSaldoException, IllegalDocumentStateException;

    AccountSaldoGraph getAccountSaldoGraph(Deposit deposit);

    AccountSaldoGraph getAccountAvailableToOutcomeSaldoGraph(Deposit deposit);
}
