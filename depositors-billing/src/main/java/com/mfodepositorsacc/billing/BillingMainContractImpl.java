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
import com.mfodepositorsacc.billing.repository.DepositOutcomeDocumentRepository;
import com.mfodepositorsacc.billing.repository.DocumentPayInAccountDocumentRepository;
import com.mfodepositorsacc.billing.service.AccountUtils;
import com.mfodepositorsacc.billing.service.BillingService;
import com.mfodepositorsacc.billing.service.DocumentsService;
import com.mfodepositorsacc.billing.specifications.DocumentOutcomeSpecification;
import com.mfodepositorsacc.billing.specifications.DocumentPayInAccountSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by berz on 30.12.14.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BillingMainContractImpl implements BillingMainContract {

    @Autowired
    BillingService billingService;

    @Autowired
    DocumentsService documentsService;

    @Autowired
    DocumentPayInAccountDocumentRepository documentPayInAccountDocumentRepository;

    @Autowired
    DepositOutcomeDocumentRepository depositOutcomeDocumentRepository;

    @Autowired
    AccountUtils accountUtils;

    @Override
    public DepositPayInAccountDocument doDepositBasePayInAccountRequest(Long depositId, BigDecimal sum) throws DuplicatePaymentException {

        if(documentPayInAccountDocumentRepository.count(DocumentPayInAccountSpecification.newBase(depositId)) > 0){
            throw new DuplicatePaymentException("duplicate base pay in for #".concat(depositId.toString()));
        }

        return billingService.doDepositPayInAccountRequest(depositId, sum, DepositPayInAccountDocument.Type.BASE);
    }

    @Override
    public DepositPayInAccountDocument doDepositAdditionalPayInAccountRequest(Long depositId, BigDecimal sum) {
        return billingService.doDepositPayInAccountRequest(depositId, sum, DepositPayInAccountDocument.Type.ADDITIONAL);
    }

    @Override
    public void doDepositPayInAccountConfirm(DepositPayInAccountDocument depositPayInAccountDocument) throws IllegalDocumentStateException {
        billingService.doDepositPayInAccountConfirm(depositPayInAccountDocument);
    }

    @Override
    public void doDepositPayInMonthlyProfit(Long depositId, BigDecimal sum, Date nextCapitalization) {
        billingService.doDepositPayInMonthlyProfit(depositId, sum, nextCapitalization);
    }

    @Override
    public DepositOutcomeDocument doDepositEarlyOutcomeRequest(Long depositId, BigDecimal sum) throws RedSaldoException {
        return billingService.doDepositOutcomeRequest(depositId, sum, DepositOutcomeDocument.Type.EARLY);
    }

    @Override
    public DepositOutcomeDocument doDepositClosingOutcomeRequest(Long depositId, BigDecimal sum) throws DuplicatePaymentException, RedSaldoException {
        if(depositOutcomeDocumentRepository.count(DocumentOutcomeSpecification.closing(depositId)) > 0){
            throw new DuplicatePaymentException("duplicate payment for ".concat(depositId.toString()));
        }

        return billingService.doDepositOutcomeRequest(depositId, sum, DepositOutcomeDocument.Type.CLOSING);
    }

    @Override
    public void doDepositOutcomeConfirm(DepositOutcomeDocument depositOutcomeDocument) throws RedSaldoException, IllegalDocumentStateException {
        billingService.doDepositOutcomeConfirm(depositOutcomeDocument);
    }

    @Override
    public BigDecimal getDepositSaldo(Long depositId) {
        return billingService.getDepositSaldo(depositId).abs();
    }

    @Override
    public DepositPayInAccountDocument getPayInAccountDocumentByNum(String num) throws DocumentNotFoundException {
        DepositPayInAccountDocument depositPayInAccountDocument = documentPayInAccountDocumentRepository.findByNum(num);

        if(depositPayInAccountDocument == null){
            throw new DocumentNotFoundException("document #".concat(num).concat(" not found"));
        }

        return depositPayInAccountDocument;
    }

    @Override
    public DepositOutcomeDocument getDepositOutcomeDocumentByNum(String docNum) throws DocumentNotFoundException {
        DepositOutcomeDocument depositOutcomeDocument = depositOutcomeDocumentRepository.findByNum(docNum);

        if(depositOutcomeDocument == null){
            throw new DocumentNotFoundException("document #".concat(docNum).concat(" not found"));
        }

        return depositOutcomeDocument;
    }

    @Override
    public Long countNewBasePayInRequests() {
        return documentsService.countNewBasePayInRequests();
    }

    @Override
    public Long countNewAdditionalPayInRequests() {
        return documentsService.countNewAdditionalPayInRequests();
    }

    @Override
    public Long countNewEarlyOutcomeRequests() {
        return documentsService.countNewEarlyOutcomeRequests();
    }

    @Override
    public Page<DepositPayInAccountDocument> newBasePayInRequests(Pageable pageable) {
        return documentsService.newBasePayInRequests(pageable);
    }

    @Override
    public Page<DepositPayInAccountDocument> newAdditionalPayInRequests(Pageable pageable) {
        return documentsService.newAdditionalPayInRequests(pageable);
    }

    @Override
    public Page<DepositOutcomeDocument> newEarlyOutcomeRequests(Pageable pageable) {
        return documentsService.newEarlyOutcomeRequests(pageable);
    }

    @Override
    public DepositPayInAccountDocument getBasePayInAccountDocumentByDepositId(Long depositId) {
        return documentsService.getBasePayInDocument(depositId);
    }

    @Override
    public List<Document> getCapitalizationLog(Long depositId) {
        return documentsService.getCapitalizationLog(depositId);
    }

    @Override
    public List<Document> getPayInAccountLog(Long depositId) {
        return documentsService.getPayInAccountLog(depositId);
    }

    @Override
    public List<Document> getPayInAccountRequestsUndoneLog(Long depositId) {
        return documentsService.getPayInAccountRequestsUndoneLog(depositId);
    }

    @Override
    public void cancelRequestBeforeMoneyTransferByDocNumber(String code) throws DocumentNotFoundException, DocumentAlreadyProcessingException, RedSaldoException, IllegalDocumentStateException {
        documentsService.cancelRequestBeforeMoneyTransferByDocNumber(code);
    }

    @Override
    public List<Document> getOutcomeLog(Long depositId) {
        return documentsService.getOutcomeLog(depositId);
    }

    @Override
    public List<Document> getOutcomeUndoneLog(Long depositId) {
        return documentsService.getOutcomeUndoneLog(depositId);
    }

    @Override
    public BigDecimal getDepositLockedToOutcomeSaldo(Long depositId) {
        return billingService.getDepositSaldoLockedToOutcome(depositId).abs();
    }

    @Override
    public List<AccountHistory> getDepositAccHistory(Long depositId, Date after) {
        return accountUtils.getDepositAccHistory(depositId, after);
    }

    @Override
    public List<AccountHistory> getDepositAvailableAccHistory(Long depositId, Date after) {
        return accountUtils.getDepositAvailableAccHistory(depositId, after);
    }

    @Override
    public BigDecimal getDepositAvailableSaldo(Long depositId) {
        return billingService.getDepositAvailableSaldo(depositId).abs();
    }


}
