package com.mfodepositorsacc.service;

import com.mfodepositorsacc.accountingmachine.dmodel.Account;
import com.mfodepositorsacc.accountingmachine.dmodel.AccountHistory;
import com.mfodepositorsacc.accountingmachine.dmodel.Document;
import com.mfodepositorsacc.accountingmachine.exception.RedSaldoException;
import com.mfodepositorsacc.billing.BillingMainContract;
import com.mfodepositorsacc.billing.dmodel.DepositOutcomeDocument;
import com.mfodepositorsacc.billing.dmodel.DepositPayInAccountDocument;
import com.mfodepositorsacc.billing.exception.DocumentAlreadyProcessingException;
import com.mfodepositorsacc.billing.exception.DocumentNotFoundException;
import com.mfodepositorsacc.billing.exception.DuplicatePaymentException;
import com.mfodepositorsacc.billing.exception.IllegalDocumentStateException;
import com.mfodepositorsacc.dmodel.Deposit;
import com.mfodepositorsacc.dto.AccountSaldoGraph;
import com.mfodepositorsacc.dto.AccountSaldoGraphDescriptionElement;
import com.mfodepositorsacc.dto.AccountSaldoGraphElement;
import com.mfodepositorsacc.repository.DepositRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by berz on 19.01.15.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BillingSystemUtilsImpl implements BillingSystemUtils {

    @Autowired
    BillingMainContract billingMainContract;

    @Autowired
    DepositRepository depositRepository;

    @Override
    public DepositPayInAccountDocument doDepositBasePayInAccountRequest(Long depositId, BigDecimal sum) throws DuplicatePaymentException {
        return billingMainContract.doDepositBasePayInAccountRequest(depositId, sum);
    }

    @Override
    public DepositPayInAccountDocument doDepositAdditionalPayInAccountRequest(Long depositId, BigDecimal sum) {
        return billingMainContract.doDepositAdditionalPayInAccountRequest(depositId, sum);
    }

    @Override
    public void doDepositPayInAccountConfirm(DepositPayInAccountDocument depositPayInAccountDocument) throws IllegalDocumentStateException {
        billingMainContract.doDepositPayInAccountConfirm(depositPayInAccountDocument);
    }

    @Override
    public void doDepositPayInMonthlyProfit(Long depositId, BigDecimal sum, Date nextCapitalization) {
        billingMainContract.doDepositPayInMonthlyProfit(depositId, sum, nextCapitalization);
    }

    @Override
    public DepositOutcomeDocument doDepositEarlyOutcomeRequest(Long depositId, BigDecimal sum) throws RedSaldoException {
        return billingMainContract.doDepositEarlyOutcomeRequest(depositId, sum);
    }

    @Override
    public DepositOutcomeDocument doDepositClosingOutcomeRequest(Long depositId, BigDecimal sum) throws DuplicatePaymentException, RedSaldoException {
        return billingMainContract.doDepositClosingOutcomeRequest(depositId, sum);
    }

    @Override
    public void doDepositOutcomeConfirm(DepositOutcomeDocument depositOutcomeDocument) throws RedSaldoException, IllegalDocumentStateException {
        billingMainContract.doDepositOutcomeConfirm(depositOutcomeDocument);
    }

    @Override
    public BigDecimal getDepositSaldo(Long depositId) {
        return billingMainContract.getDepositSaldo(depositId);
    }

    @Override
    public BigDecimal getDepositAvailableSaldo(Long depositId){
        return billingMainContract.getDepositAvailableSaldo(depositId);
    }

    @Override
    public BigDecimal getDepositLockedToOutcomeSaldo(Long depositId) {
        return billingMainContract.getDepositLockedToOutcomeSaldo(depositId);
    }

    @Override
    public DepositPayInAccountDocument getPayInAccountDocumentByNum(String num) throws DocumentNotFoundException {
        return billingMainContract.getPayInAccountDocumentByNum(num);
    }

    @Override
    public Long countNewBasePayInRequests() {
        return billingMainContract.countNewBasePayInRequests();
    }

    @Override
    public Long countNewAdditionalPayInRequests() {
        return billingMainContract.countNewAdditionalPayInRequests();
    }

    @Override
    public Long countNewEarlyOutcomeRequests() {
        return billingMainContract.countNewEarlyOutcomeRequests();
    }

    @Override
    public Page<DepositPayInAccountDocument> newBasePayInRequests(Pageable pageable) {
        return billingMainContract.newBasePayInRequests(pageable);
    }

    @Override
    public Page<DepositPayInAccountDocument> newAdditionalPayInRequests(Pageable pageable) {
        return billingMainContract.newAdditionalPayInRequests(pageable);
    }

    @Override
    public Page<DepositOutcomeDocument> newEarlyOutcomeRequests(Pageable pageable) {
        return billingMainContract.newEarlyOutcomeRequests(pageable);
    }

    @Override
    public void doDepositPayInAccountConfirmByDocNumber(String docNum) throws DocumentNotFoundException, IllegalDocumentStateException {
        DepositPayInAccountDocument depositPayInAccountDocument = billingMainContract.getPayInAccountDocumentByNum(docNum);
        billingMainContract.doDepositPayInAccountConfirm(depositPayInAccountDocument);

        if(depositPayInAccountDocument.getType().equals(DepositPayInAccountDocument.Type.BASE) &&
                depositPayInAccountDocument != null &&
                depositPayInAccountDocument.getDepositId() != null){
            Deposit deposit = depositRepository.findOne(depositPayInAccountDocument.getDepositId());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.MONTH, 1);

            deposit.setNextCapitalization(calendar.getTime());

            depositRepository.save(deposit);
        }
    }

    @Override
    public DepositPayInAccountDocument getBasePayInAccountDocumentByDepositId(Long id) {
        DepositPayInAccountDocument depositPayInAccountDocument = billingMainContract.getBasePayInAccountDocumentByDepositId(id);
        return depositPayInAccountDocument;
    }

    @Override
    public List<Document> getCapitalizationLog(Deposit deposit) {
        return billingMainContract.getCapitalizationLog(deposit.getId());
    }

    @Override
    public List<Document> getPayInAccountLog(Deposit deposit) {
        return billingMainContract.getPayInAccountLog(deposit.getId());
    }

    @Override
    public List<Document> getPayInAccountRequestsUndoneLog(Deposit deposit) {
        return billingMainContract.getPayInAccountRequestsUndoneLog(deposit.getId());
    }

    @Override
    public void cancelRequestBeforeMoneyTransferByDocNumber(String code) throws DocumentNotFoundException, DocumentAlreadyProcessingException, RedSaldoException, IllegalDocumentStateException {
        billingMainContract.cancelRequestBeforeMoneyTransferByDocNumber(code);
    }

    @Override
    public List<Document> getOutcomeLog(Deposit deposit) {
        return billingMainContract.getOutcomeLog(deposit.getId());
    }

    @Override
    public List<Document> getOutcomeUndoneLog(Deposit deposit) {
        return billingMainContract.getOutcomeUndoneLog(deposit.getId());
    }

    @Override
    public void doDepositOutcomeConfirmByDocNumber(String docNum) throws DocumentNotFoundException, RedSaldoException, IllegalDocumentStateException {
        DepositOutcomeDocument depositOutcomeDocument = billingMainContract.getDepositOutcomeDocumentByNum(docNum);

        doDepositOutcomeConfirm(depositOutcomeDocument);
    }


    private AccountSaldoGraph getAccountSaldoGraphByHistory(List<AccountHistory> accountHistory, BigDecimal saldo){
        List<AccountSaldoGraphElement> accountSaldoGraphElements = new LinkedList<AccountSaldoGraphElement>();
        List<AccountSaldoGraphDescriptionElement> accountSaldoGraphDescriptionElements = new LinkedList<AccountSaldoGraphDescriptionElement>();
        HashMap<String, BigDecimal> saldoByCode = new LinkedHashMap<String, BigDecimal>();

        Date dtAcc;
        BigDecimal minSaldo = null;
        BigDecimal maxSaldo = null;
        if(!accountHistory.isEmpty()){
            Iterator<AccountHistory> accountHistoryIterator = accountHistory.iterator();

            AccountHistory accountHistory1;

            while(accountHistoryIterator.hasNext()){
                accountHistory1 = accountHistoryIterator.next();

                if(
                        accountHistory.size() > 2 &&
                                accountHistory1.getSaldo().abs().compareTo(BigDecimal.ZERO) < 1 &&
                                accountHistoryIterator.hasNext()
                        ){
                    accountHistory1 = accountHistoryIterator.next();
                }

                String code = accountHistory1.getAccount().getCode();
                if(saldoByCode.containsKey(code)){
                    saldoByCode.remove(code);
                }
                saldoByCode.put(code, accountHistory1.getSaldo().abs());

                BigDecimal sum = BigDecimal.ZERO;
                for(BigDecimal s : saldoByCode.values()){
                    sum = sum.add(s);
                }

                if(
                        minSaldo == null || sum.compareTo(minSaldo) < 1
                        ){
                    minSaldo = sum;
                }

                if(
                        maxSaldo == null || sum.compareTo(maxSaldo) > -1
                        ){
                    maxSaldo = sum;
                }


                dtAcc = accountHistory1.getAccountTransaction().getDtAcc();
                accountSaldoGraphElements.add(new AccountSaldoGraphElement(dtAcc.getTime(), sum));
            }

            accountSaldoGraphElements.add(new AccountSaldoGraphElement((new Date()).getTime(), saldo));
        }

        if(!accountSaldoGraphElements.isEmpty()){
            Date dtDesc = new Date(accountSaldoGraphElements.iterator().next().getTimestamp());
            Calendar cal = Calendar.getInstance();
            cal.setTime(dtDesc);
            cal.set(Calendar.DAY_OF_MONTH, 1);

            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.applyPattern("MM.yyyy");

            while (
                    cal.getTime().compareTo(
                            new Date(
                                    accountSaldoGraphElements.get(
                                            accountSaldoGraphElements.size() - 1
                                    ).getTimestamp()
                            )
                    ) < 1){

                accountSaldoGraphDescriptionElements.add(
                        new AccountSaldoGraphDescriptionElement(cal.getTime().getTime(), sdf.format(cal.getTime()))
                );
                cal.add(Calendar.MONTH, 1);
            }
        }

        return new AccountSaldoGraph(accountSaldoGraphElements, accountSaldoGraphDescriptionElements, minSaldo, maxSaldo);
    }

    @Override
    public AccountSaldoGraph getAccountSaldoGraph(Deposit deposit) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, -2);

        List<AccountHistory> accountHistory = billingMainContract.getDepositAccHistory(deposit.getId(), calendar.getTime());

        return getAccountSaldoGraphByHistory(accountHistory, getDepositSaldo(deposit.getId()));
    }

    @Override
    public AccountSaldoGraph getAccountAvailableToOutcomeSaldoGraph(Deposit deposit){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, -2);

        List<AccountHistory> accountHistory = billingMainContract.getDepositAvailableAccHistory(deposit.getId(), calendar.getTime());

        return getAccountSaldoGraphByHistory(accountHistory, getDepositAvailableSaldo(deposit.getId()));
    }



 /*   @Override
    public Page<ClientPayInAccountDocumentForMain> getClientPayInDocsForMainByBillingDocs(Page<DepositPayInAccountDocument> clientPayInAccountDocuments) {
        List<ClientPayInAccountDocumentForMain> clientPayInAccountDocumentForMainList = new LinkedList<ClientPayInAccountDocumentForMain>();

        for(DepositPayInAccountDocument clientPayInAccountDocument : clientPayInAccountDocuments){
            ClientPayInAccountDocumentForMain clientPayInAccountDocumentForMain = new ClientPayInAccountDocumentForMain(clientPayInAccountDocument);



            clientPayInAccountDocumentForMainList.add(clientPayInAccountDocumentForMain);
        }

        return new PageImpl<ClientPayInAccountDocumentForMain>(clientPayInAccountDocumentForMainList);
    }

    @Override
    public Page<ClientPayInAccountDocumentForMain> getClientPayInDocsForMainPage(Pageable pageable) {
        return getClientPayInDocsForMainByBillingDocs(billingMainContract.getClientsPayInDocumentsActual(pageable));
    }*/
}
