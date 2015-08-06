package com.mfodepositorsacc.billing.service;

import com.mfodepositorsacc.accountingmachine.dmodel.Account;
import com.mfodepositorsacc.accountingmachine.dmodel.AccountTransaction;
import com.mfodepositorsacc.accountingmachine.dmodel.Document;
import com.mfodepositorsacc.accountingmachine.exception.RedSaldoException;
import com.mfodepositorsacc.accountingmachine.service.AccountMachine;
import com.mfodepositorsacc.accountingmachine.utils.Assert;
import com.mfodepositorsacc.billing.dmodel.DepositOutcomeDocument;
import com.mfodepositorsacc.billing.dmodel.DepositPayInAccountDocument;
import com.mfodepositorsacc.billing.dmodel.DepositPayMonthlyProfitDocument;
import com.mfodepositorsacc.billing.exception.DocumentAlreadyProcessingException;
import com.mfodepositorsacc.billing.exception.DocumentNotFoundException;
import com.mfodepositorsacc.billing.exception.IllegalDocumentStateException;
import com.mfodepositorsacc.billing.repository.AccountHistoryRepository;
import com.mfodepositorsacc.billing.repository.DepositOutcomeDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;


/**
 * Created by berz on 17.01.15.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BillingServiceImpl implements BillingService {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    AccountUtils accountUtils;

    @Autowired
    AccountMachine accountMachine;

    @Autowired
    AccountHistoryRepository accountHistoryRepository;

    @Autowired
    DepositOutcomeDocumentRepository depositOutcomeDocumentRepository;

    /* @Override
     @Transactional
     public void doClientPaymentForOrder(Long clientId, Long orderId, BigDecimal sum) throws RedSaldoException {
         ClientPaymentDocument clientPaymentDocument = new ClientPaymentDocument();
         clientPaymentDocument.setState(ClientPaymentDocument.State.NEW);
         clientPaymentDocument.setDtmCreate(new Date());
         clientPaymentDocument.setDtmUpdate(new Date());
         clientPaymentDocument.setDtAcc(new Date());
         clientPaymentDocument.setOrderId(orderId);
         clientPaymentDocument.setClientId(clientId);
         clientPaymentDocument.setSum(sum);

         clientPaymentDocument.setNum(
                 "ord-pay#"
                         .concat(clientId.toString())
                         .concat(".")
                         .concat(orderId.toString())
                         .concat(".")
                         .concat(getShortRandomString())
         );

         entityManager.persist(clientPaymentDocument);

         Account clientAcc = accountUtils.getDepositAccount(clientId);
         Account buffer = accountUtils.getBufferAccount();

         AccountTransaction tr = new AccountTransaction(
                 clientAcc, buffer, clientPaymentDocument.getSum(), clientPaymentDocument.getDtAcc(),
                 AccountTransaction.TransactionType.CLIENT_PAID, clientPaymentDocument
         );

         /*try {
             accountMachine.doTransaction(tr);
         } catch (RedSaldoException e) {
             throw new PaymentNotExecutedException("not enough money in account client#".concat(depositId.toString()), PaymentNotExecutedException.Reason.NOT_ENOUGH_MONEY);
         }*/
        /*
        accountMachine.doTransaction(tr);

        clientPaymentDocument.setState(ClientPaymentDocument.State.DONE);
        clientPaymentDocument.setDtmUpdate(new Date());
        entityManager.merge(clientPaymentDocument);

    }
*/
    @Override
    @Transactional
    public DepositPayInAccountDocument doDepositPayInAccountRequest(Long depositId, BigDecimal sum, DepositPayInAccountDocument.Type type) {
        DepositPayInAccountDocument depositPayInAccountDocument = new DepositPayInAccountDocument();
        depositPayInAccountDocument.setState(DepositPayInAccountDocument.State.NEW);
        depositPayInAccountDocument.setDepositId(depositId);
        depositPayInAccountDocument.setDtmCreate(new Date());
        depositPayInAccountDocument.setDtmUpdate(new Date());
        depositPayInAccountDocument.setSum(sum);
        depositPayInAccountDocument.setType(type);
        depositPayInAccountDocument.setDtAcc(new Date());
        depositPayInAccountDocument.setNum(
                "payin#"
                        .concat(depositId.toString())
                        .concat(".")
                        .concat(this.getShortRandomString())
        );

        entityManager.persist(depositPayInAccountDocument);

        return depositPayInAccountDocument;
    }

    private String getRandomString() {
        return UUID.randomUUID().toString();
    }

    private String getShortRandomString() {
        UUID uuid = UUID.randomUUID();
        long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
        return Long.toString(l, Character.MAX_RADIX);
    }

    @Override
    @Transactional
    public void doDepositPayInAccountConfirm(DepositPayInAccountDocument depositPayInAccountDocument) throws IllegalDocumentStateException {
        Assert.isNotNull(depositPayInAccountDocument.getType());
        Assert.isNotNull(depositPayInAccountDocument.getDepositId());

        if (!depositPayInAccountDocument.getState().equals(DepositPayInAccountDocument.State.NEW)) {
            throw new IllegalDocumentStateException("document is not in NEW status already");
        }

        depositPayInAccountDocument.setDtAcc(new Date());

        Account clientAcc = (depositPayInAccountDocument.getType().equals(DepositPayInAccountDocument.Type.BASE))?
                accountUtils.getDepositInitialSubAccount(depositPayInAccountDocument.getDepositId())     :
            accountUtils.getDepositAccount(depositPayInAccountDocument.getDepositId());
        Account buffer = accountUtils.getBufferAccount();

        AccountTransaction tr = new AccountTransaction(
                clientAcc, buffer, depositPayInAccountDocument.getSum(), depositPayInAccountDocument.getDtAcc(),
                AccountTransaction.TransactionType.DEPOSIT_PAY_IN_ACC, depositPayInAccountDocument
        );

        try {
            accountMachine.doTransaction(tr);
        } catch (RedSaldoException e) {
            throw new RuntimeException("unexpected red saldo when adding some money to non-negative acc!");
        }

        depositPayInAccountDocument.setState(DepositPayInAccountDocument.State.DONE);
        entityManager.merge(depositPayInAccountDocument);

    }

    @Override
    @Transactional
    public void doDepositPayInMonthlyProfit(Long depositId, BigDecimal sum, Date nextCapitalization) {
        Date dt = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);

        Date capitalizationDt = nextCapitalization != null ? nextCapitalization : dt;
        Calendar capitalizatonCal = Calendar.getInstance();
        capitalizatonCal.setTime(capitalizationDt);

        DepositPayMonthlyProfitDocument depositPayMonthlyProfitDocument = new DepositPayMonthlyProfitDocument();
        depositPayMonthlyProfitDocument.setDepositId(depositId);
        depositPayMonthlyProfitDocument.setState(DepositPayMonthlyProfitDocument.State.NEW);
        depositPayMonthlyProfitDocument.setDtmCreate(dt);
        depositPayMonthlyProfitDocument.setDtAcc(capitalizationDt);
        depositPayMonthlyProfitDocument.setSum(sum);
        depositPayMonthlyProfitDocument.setNum(
                "percens#".concat(depositId.toString()) + "." + capitalizatonCal.get(Calendar.MONTH) + "." + capitalizatonCal.get(Calendar.YEAR)
        );

        entityManager.persist(depositPayMonthlyProfitDocument);

        Account depositAcc = accountUtils.getDepositAccount(depositId);
        Account buffer = accountUtils.getBufferAccount();

        AccountTransaction tr = new AccountTransaction(
                depositAcc, buffer, sum, capitalizationDt, AccountTransaction.TransactionType.DEPOSIT_PERCENTS_PAY, depositPayMonthlyProfitDocument
        );

        try {
            accountMachine.doTransaction(tr);
        } catch (RedSaldoException e) {
            throw new RuntimeException("unexpected red saldo!");
        }

        depositPayMonthlyProfitDocument.setState(DepositPayMonthlyProfitDocument.State.DONE);
        depositPayMonthlyProfitDocument.setDtmUpdate(new Date());
        entityManager.merge(depositPayMonthlyProfitDocument);
    }

    @Override
    @Transactional
    public DepositOutcomeDocument doDepositOutcomeRequest(Long depositId, BigDecimal sum, DepositOutcomeDocument.Type type) throws RedSaldoException {
        Assert.isNotNull(type);
        Assert.isTrue(sum != null && sum.compareTo(BigDecimal.ZERO) > 0);
        Assert.isNotNull(depositId);

        DepositOutcomeDocument depositOutcomeDocument = new DepositOutcomeDocument();
        depositOutcomeDocument.setDepositId(depositId);
        depositOutcomeDocument.setState(DepositOutcomeDocument.State.NEW);
        depositOutcomeDocument.setType(type);
        depositOutcomeDocument.setNum("outcome#".concat(depositId.toString()).concat(".").concat(type.toString()).concat(".").concat(getShortRandomString()));
        depositOutcomeDocument.setSum(sum);
        depositOutcomeDocument.setDtmCreate(new Date());
        depositOutcomeDocument.setDtAcc(new Date());

        entityManager.persist(depositOutcomeDocument);

        Account mainAcc = accountUtils.getDepositAccount(depositId);
        Account subOutcome = accountUtils.getDepositSubAccountForOutcome(depositId);

        AccountTransaction tr = new AccountTransaction(
                subOutcome, mainAcc, sum, new Date(), AccountTransaction.TransactionType.DEPOSIT_OUTCOME_LOCK_MONEY, depositOutcomeDocument
        );

        accountMachine.doTransaction(tr);

        return depositOutcomeDocument;
    }

    @Override
    public void doDepositOutcomeConfirm(DepositOutcomeDocument depositOutcomeDocument) throws RedSaldoException, IllegalDocumentStateException {
        Assert.isNotNull(depositOutcomeDocument.getDepositId());
        Assert.isNotNull(depositOutcomeDocument.getType());
        Assert.isNotNull(depositOutcomeDocument.getSum());

        if (!depositOutcomeDocument.getState().equals(DepositOutcomeDocument.State.NEW)) {
            throw new IllegalDocumentStateException("document is not in NEW status already");
        }

        depositOutcomeDocument.setDtAcc(new Date());

        Account depositSubAccOutcome = accountUtils.getDepositSubAccountForOutcome(depositOutcomeDocument.getDepositId());
        Account buffer = accountUtils.getBufferAccount();

        AccountTransaction tr = new AccountTransaction(
                buffer, depositSubAccOutcome, depositOutcomeDocument.getSum(), depositOutcomeDocument.getDtAcc(), AccountTransaction.TransactionType.DEPOSIT_OUTCOME, depositOutcomeDocument
        );

        accountMachine.doTransaction(tr);

        depositOutcomeDocument.setState(DepositOutcomeDocument.State.DONE);

        entityManager.merge(depositOutcomeDocument);
    }

    @Override
    public void cancelDepositOutcome(DepositOutcomeDocument depositOutcomeDocument) throws RedSaldoException, DocumentAlreadyProcessingException, IllegalDocumentStateException {
        Assert.isNotNull(depositOutcomeDocument.getDepositId());
        Assert.isNotNull(depositOutcomeDocument.getType());
        Assert.isNotNull(depositOutcomeDocument.getSum());

        if(!depositOutcomeDocument.getState().equals(DepositOutcomeDocument.State.NEW)){
            throw new IllegalDocumentStateException("document is not in NEW status already!");
        }

        for (AccountTransaction tr : depositOutcomeDocument.getAccountTransactions()) {
            if (tr.getType().equals(AccountTransaction.TransactionType.DEPOSIT_OUTCOME_LOCK_MONEY)) {
                // Блокировку средств отменяем
                accountMachine.undoTransaction(tr);
            } else {
                // Не должно быть других транзакций
                throw new DocumentAlreadyProcessingException("Money transfer already started!");
            }
        }

        depositOutcomeDocument.setState(DepositOutcomeDocument.State.CANCELLED);
        depositOutcomeDocumentRepository.save(depositOutcomeDocument);
    }

    @Override
    public BigDecimal getDepositSaldo(Long depositId) {

        Account clientAcc = accountUtils.getDepositAccount(depositId);
        Account initialAcc = accountUtils.getDepositInitialSubAccount(depositId);

        return accountMachine.getSaldo(clientAcc, null)
                .add(accountMachine.getSaldo(initialAcc, null));
    }

    @Override
    @Transactional
    public DepositPayInAccountDocument getPayInAccountDocumentByNum(String num) throws DocumentNotFoundException {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<DepositPayInAccountDocument> criteriaQuery = builder.createQuery(DepositPayInAccountDocument.class);
        Root<DepositPayInAccountDocument> root = criteriaQuery.from(DepositPayInAccountDocument.class);

        criteriaQuery.where(builder.equal(root.get("num"), num));

        Query query = entityManager.createQuery(criteriaQuery).setFirstResult(0).setMaxResults(1);

        if (!query.getResultList().isEmpty()) {
            return (DepositPayInAccountDocument) query.getResultList().get(0);
        }

        throw new DocumentNotFoundException("cannot find document with num = ".concat(num));
    }

    @Override
    public BigDecimal getDepositSaldoLockedToOutcome(Long depositId) {
        Account account = accountUtils.getDepositSubAccountForOutcome(depositId);

        return accountMachine.getSaldo(account, null);
    }

    @Override
    public Document getDocumentByNum(String code) throws DocumentNotFoundException {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Document> criteriaQuery = builder.createQuery(Document.class);
        Root<Document> root = criteriaQuery.from(Document.class);

        criteriaQuery.where(builder.equal(root.get("num"), code));

        Query query = entityManager.createQuery(criteriaQuery).setFirstResult(0).setMaxResults(1);

        if (!query.getResultList().isEmpty()) {
            return (Document) query.getResultList().get(0);
        }

        throw new DocumentNotFoundException("cannot find document with num = ".concat(code));
    }

    @Override
    public BigDecimal getDepositAvailableSaldo(Long depositId) {
        Account clientAcc = accountUtils.getDepositAccount(depositId);
        return accountMachine.getSaldo(clientAcc, null);
    }

  /*  @Override
    public Page<AccountHistory> getAccountHistoryByClientId(Long id, Pageable pageable) {

        Account clientAccount = accountUtils.getDepositAccount(id);

        if(clientAccount != null){
            return accountHistoryRepository.findByAccountOrderByDtmUpdateDesc(clientAccount, pageable);
        }
        else return null;
    }
*/

}
