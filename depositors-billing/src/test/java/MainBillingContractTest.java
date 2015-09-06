import com.mfodepositorsacc.accountingmachine.dmodel.Account;
import com.mfodepositorsacc.accountingmachine.dmodel.AccountTransaction;
import com.mfodepositorsacc.accountingmachine.exception.RedSaldoException;
import com.mfodepositorsacc.accountingmachine.service.AccountMachine;
import com.mfodepositorsacc.billing.BillingMainContract;
import com.mfodepositorsacc.billing.dmodel.DepositOutcomeDocument;
import com.mfodepositorsacc.billing.dmodel.DepositPayInAccountDocument;
import com.mfodepositorsacc.billing.exception.DuplicatePaymentException;
import com.mfodepositorsacc.billing.service.AccountUtils;
import com.mfodepositorsacc.billing.service.BillingService;
import com.mfodepositorsacc.billing.service.DocumentsService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by berz on 12.01.15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(classes = {TestBillingBeanConfiguration.class, TestBillingJPAConfiguration.class})
public class MainBillingContractTest {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    BillingMainContract billingMainContract;

    @Autowired
    AccountMachine accountMachine;

    @Autowired
    AccountUtils accountUtils;

    @Autowired
    BillingService billingService;

    @Autowired
    DocumentsService documentsService;

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

 /*   private void createTestSythAcc(Account.Behavior behavior, String code) {
        Account account = new Account();
        account.setBehavior(Account.Behavior.A);
        account.setCode(code);
        account.setIdDeposit(0);
        account.setSynthetic(true);
        account.setBehavior(behavior);
        account.setSchema("{idDeposit:Integer}");

        entityManager.persist(account);
    }
*/


    @Test
    public void alwaysOkTest(){
        System.out.println("always ok");
    }


/*    @Test
    public void simpleSeekAcc() {

        LinkedHashMap<String, String> analytics = new LinkedHashMap<String, String>();
        analytics.put("code", "deposit_main");
        analytics.put("idDeposit", "1");

        Account acc1 = accountMachine.seekAccount(analytics);

        assert acc1.getIdDeposit().toString().equals(analytics.get("idDeposit"));
        assert acc1.getBehavior().equals(Account.Behavior.P);
        assert acc1.getCode().equals(analytics.get("code"));

        System.out.println("TEST:: " + acc1.getSchema());
    }

    @Test
    public void accUtilsSeekMainAcc() {

        Account acc1 = accountUtils.getDepositAccount(1l);

        assert acc1.getIdDeposit().toString().equals("1");
        assert acc1.getBehavior().equals(Account.Behavior.P);
        assert acc1.getCode().equals("deposit_main");

    }

    @Test
    public void duplicateBasePayIn() throws DuplicatePaymentException {
        thrown.expect(DuplicatePaymentException.class);

        Long depositId = 3000l;
        BigDecimal sumToPayBase = BigDecimal.valueOf(5000000);

        DepositPayInAccountDocument depositPayInAccountDocument = billingMainContract.doDepositBasePayInAccountRequest(depositId, sumToPayBase);

        // Еще разок, тут должно вывалиться исключение
        DepositPayInAccountDocument depositPayInAccountDocument1 = billingMainContract.doDepositBasePayInAccountRequest(depositId, sumToPayBase);

    }

    @Test
    public void duplicateClosingOutcome() throws DuplicatePaymentException, Exception {
        thrown.expect(DuplicatePaymentException.class);

        Long depositId = 3000l;
        BigDecimal sumToPayBase = BigDecimal.valueOf(5000000);
        BigDecimal sumToOutcome = BigDecimal.valueOf(5000000);

        DepositPayInAccountDocument depositPayInAccountDocument = null;
        try {
            depositPayInAccountDocument = billingMainContract.doDepositBasePayInAccountRequest(depositId, sumToPayBase);
        } catch (DuplicatePaymentException e) {
            // Мы ожидаем исключения не на этом участке
            //throw new Exception("Исключение брошено не там, где ожидалось. Мы проверяем наличие исключения при повторном запросе на закрытие вклада");
        }
        billingMainContract.doDepositPayInAccountConfirm(depositPayInAccountDocument);

        DepositOutcomeDocument depositOutcomeDocument = billingMainContract.doDepositClosingOutcomeRequest(depositId, sumToOutcome);

        // И еще разок
        DepositOutcomeDocument depositOutcomeDocument1 = billingMainContract.doDepositClosingOutcomeRequest(depositId, sumToOutcome);

    }

    @Test
    public void depositPayInAllTest() throws Exception {

        Long depositId = 3000l;
        BigDecimal sumToPayBase = BigDecimal.valueOf(5000000);
        BigDecimal sumAddt = BigDecimal.valueOf(300000);
        BigDecimal sumToOut = BigDecimal.valueOf(1200000);

        DepositPayInAccountDocument depositPayInAccountDocument = billingMainContract.doDepositBasePayInAccountRequest(depositId, sumToPayBase);

        // Деньги не начислены (был только запрос на пополнение)
        assert !sumToPayBase.equals(billingService.getDepositSaldo(depositId));
        assert depositPayInAccountDocument.getDepositId().equals(depositId);
        assert depositPayInAccountDocument.getState().equals(DepositPayInAccountDocument.State.NEW);


        billingMainContract.doDepositPayInAccountConfirm(depositPayInAccountDocument);

        // Теперь деньги начислены
        if(!sumToPayBase.equals(billingService.getDepositSaldo(depositId).abs())){
            throw new Exception("wrong saldo: " + billingService.getDepositSaldo(depositId).abs() + ", must be " + sumToPayBase);
        }

        // Добавляем еще денег
        DepositPayInAccountDocument depositPayInAccountDocument1 = billingMainContract.doDepositAdditionalPayInAccountRequest(depositId, sumAddt);
        billingMainContract.doDepositPayInAccountConfirm(depositPayInAccountDocument1);

        if(!sumToPayBase.add(sumAddt).equals(billingService.getDepositSaldo(depositId).abs())){
            throw new Exception("wrong saldo: " + billingService.getDepositSaldo(depositId).abs() + ", must be " + sumToPayBase.add(sumAddt));
        }


        // Теперь забираем часть денег
        DepositOutcomeDocument depositOutcomeDocument = billingMainContract.doDepositEarlyOutcomeRequest(depositId, sumToOut);
        billingMainContract.doDepositOutcomeConfirm(depositOutcomeDocument);

        if(!sumToPayBase.add(sumAddt).add(sumToOut.negate()).equals(billingService.getDepositSaldo(depositId).abs())){
            throw new Exception("wrong saldo: " + billingService.getDepositSaldo(depositId).abs() + ", must be " + sumToPayBase.add(sumAddt).add(sumToOut.negate()));
        }
    }

    @Test
    public void depositPayMonthlyProfitTest() throws Exception {
        Long depositId = 5213l;
        BigDecimal sum = BigDecimal.valueOf(3400032);

        billingService.doDepositPayInMonthlyProfit(depositId, sum, null);

        // Теперь деньги начислены
        if(!sum.equals(billingService.getDepositSaldo(depositId).abs())){
            throw new Exception("wrong saldo: " + billingService.getDepositSaldo(depositId).abs() + ", must be " + sum);
        }
    }

    @Test
    public void depositOutcomeTest() throws Exception {
        Long depositId = 424325l;
        BigDecimal sumToBeInAcc = BigDecimal.valueOf(3500000);
        BigDecimal sumToOutcome = BigDecimal.valueOf(1500000);
        BigDecimal sumToOutcomeMore = BigDecimal.valueOf(500000);

        DepositPayInAccountDocument document = billingService.doDepositPayInAccountRequest(depositId, sumToBeInAcc, DepositPayInAccountDocument.Type.BASE);
        billingService.doDepositPayInAccountConfirm(document);
        // Теперь у нас есть деньги

        // Пробуем вывести
        DepositOutcomeDocument depositOutcomeDocument = billingService.doDepositOutcomeRequest(depositId, sumToOutcome, DepositOutcomeDocument.Type.EARLY);

        if(!billingService.getDepositSaldo(depositId).abs().equals(sumToBeInAcc.add(sumToOutcome.negate()))){
            throw new Exception("wrong saldo: money was not locked! ha");
        }

        BigDecimal lockedMoney = billingService.getDepositSaldoLockedToOutcome(depositId).abs();

        if(!lockedMoney.equals(sumToOutcome)){
            throw new Exception("wrong sum locked!");
        }

        // Средства заблокированы. Выводим

        billingService.doDepositOutcomeConfirm(depositOutcomeDocument);

        if(!billingService.getDepositSaldo(depositId).abs().equals(sumToBeInAcc.add(sumToOutcome.negate()))){
            throw new Exception("wrong saldo after confirmation!");
        }

        if(!billingService.getDepositSaldoLockedToOutcome(depositId).abs().equals(BigDecimal.ZERO)){
            throw new Exception("still have money at outcome subaccount!");
        }

        DepositOutcomeDocument depositOutcomeDocument1 = billingService.doDepositOutcomeRequest(depositId, sumToOutcomeMore, DepositOutcomeDocument.Type.EARLY);

        entityManager.flush();
        entityManager.refresh(depositOutcomeDocument1);

        List<AccountTransaction> trs = depositOutcomeDocument1.getAccountTransactions();

        if(trs.isEmpty()){
            throw new Exception("empty transactions list!");
        }

        documentsService.cancelRequestBeforeMoneyTransferByDocNumber(depositOutcomeDocument1.getNum());

        if(!billingService.getDepositSaldo(depositId).abs().setScale(0, RoundingMode.HALF_UP).equals(sumToBeInAcc.add(sumToOutcome.negate()))){
            throw new Exception(
                    "money was not returned! Account saldo: "
                            .concat(billingService.getDepositSaldo(depositId).abs().toString())
                            .concat(" expected: ")
                            .concat(sumToBeInAcc.add(sumToOutcome.negate()).toString())
            );
        }
    }

    @Test
    public void depositOutcomeBigSumTest() {
        Long depositId = 424325l;
        BigDecimal sumToBeInAcc = BigDecimal.valueOf(3500000);
        BigDecimal sumToOutcome = BigDecimal.valueOf(5500000);
        //BigDecimal sumToOutcomeMore = BigDecimal.valueOf(500000);

        DepositPayInAccountDocument document = billingService.doDepositPayInAccountRequest(depositId, sumToBeInAcc, DepositPayInAccountDocument.Type.BASE);
        billingService.doDepositPayInAccountConfirm(document);
        // Теперь у нас есть деньги

        // Пробуем вывести
        try {
            DepositOutcomeDocument depositOutcomeDocument = billingService.doDepositOutcomeRequest(depositId, sumToOutcome, DepositOutcomeDocument.Type.EARLY);
        } catch (RedSaldoException e) {
            entityManager.getTransaction().begin();
        }

    }
/*
    @Test
    public void simpleTransaction() {

        createTestSythAcc(Account.Behavior.A, "simple_acc");
        createTestSythAcc(Account.Behavior.AP, "buffer");

        LinkedHashMap<String, String> aBuffer = new LinkedHashMap<String, String>();
        aBuffer.put("code", "buffer");
        aBuffer.put("idDeposit", "0");

        LinkedHashMap<String, String> analytics1 = new LinkedHashMap<String, String>();
        analytics1.put("code", "simple_acc");
        analytics1.put("idDeposit", "1");

        LinkedHashMap<String, String> analytics2 = new LinkedHashMap<String, String>();
        analytics2.put("code", "simple_acc");
        analytics2.put("idDeposit", "2");

        Account buffer = accountMachine.seekAccount(aBuffer);

        Account acc1 = accountMachine.seekAccount(analytics1);
        Account acc2 = accountMachine.seekAccount(analytics2);

        ClientPaymentDocument transactionDoc = new ClientPaymentDocument();
        transactionDoc.setDtAcc(new Date());
        transactionDoc.setNum("00000");
        transactionDoc.setDtmCreate(new Date());
        transactionDoc.setDtmUpdate(new Date());
        transactionDoc.setSum(BigDecimal.valueOf(100l));
        transactionDoc.setState(ClientPaymentDocument.State.NEW);

        BigDecimal acc1saldo = BigDecimal.valueOf(10000l);
        BigDecimal acc2saldo = BigDecimal.valueOf(20000l);

        entityManager.persist(transactionDoc);

        AccountTransaction setAcc1Saldo = new AccountTransaction(
                buffer, acc1, acc1saldo, new Date(), AccountTransaction.TransactionType.CLIENT_PAID, transactionDoc
        );

        AccountTransaction setAcc2Saldo = new AccountTransaction(
                buffer, acc2, acc2saldo, new Date(), AccountTransaction.TransactionType.CLIENT_PAID, transactionDoc
        );

        AccountTransaction transaction = new AccountTransaction(
                acc1, acc2, transactionDoc.getSum(), new Date(), AccountTransaction.TransactionType.CLIENT_PAID, transactionDoc
        );


        try {
            accountMachine.doTransaction(setAcc1Saldo);
            accountMachine.doTransaction(setAcc2Saldo);

            accountMachine.doTransaction(transaction);
        } catch (RedSaldoException e) {
            System.out.println(e.getMessage());
        }


        System.out.println("acc1: " + accountMachine.getSaldo(acc1, null) + ", expected: " + acc1saldo.add(transactionDoc.getSum().negate()));
        System.out.println("acc2: " + accountMachine.getSaldo(acc2, null) + ", expected: " + acc2saldo.add(transactionDoc.getSum()));

        Assert.isTrue(accountMachine.getSaldo(acc1, null).compareTo(acc1saldo.add(transactionDoc.getSum().negate())) == 0);
        Assert.isTrue(accountMachine.getSaldo(acc2, null).compareTo(acc2saldo.add(transactionDoc.getSum())) == 0);
    }

    @Test
    public void testPayInAndPayments() throws PaymentNotExecutedException, RedSaldoException {

        BigDecimal acc1 = BigDecimal.valueOf(10000l);
        BigDecimal acc2 = BigDecimal.valueOf(20000l);

        BigDecimal pay11 = BigDecimal.valueOf(300l);
        BigDecimal pay12 = BigDecimal.valueOf(450l);
        BigDecimal pay2 = BigDecimal.valueOf(4500l);

        createTestSythAcc(Account.Behavior.A, "client_main");
        createTestSythAcc(Account.Behavior.AP, "buffer");

        DepositPayInAccountDocument clientPayInAccountDocument1 = billingService.doDepositPayInAccountRequest(1l, acc1);
        DepositPayInAccountDocument clientPayInAccountDocument2 = billingService.doDepositPayInAccountRequest(2l, acc2);

        System.out.println(clientPayInAccountDocument1.toString());
        System.out.println(clientPayInAccountDocument2.toString());

        billingService.doDepositPayInAccountConfirm(clientPayInAccountDocument1);
        billingService.doDepositPayInAccountConfirm(clientPayInAccountDocument2);

        billingService.doClientPaymentForOrder(1l, 3l, pay11);
        billingService.doClientPaymentForOrder(2l, 4l, pay2);
        billingService.doClientPaymentForOrder(1l, 6l, pay12);

        Assert.isTrue(billingService.getDepositSaldo(1l).equals(acc1.add(pay11.negate()).add(pay12.negate())));
        Assert.isTrue(billingService.getDepositSaldo(2l).equals(acc2.add(pay2.negate())));

    }

    @Test
    public void testRedSaldo() throws RedSaldoException {
        BigDecimal acc1 = BigDecimal.valueOf(10l);
        BigDecimal acc2 = BigDecimal.valueOf(20l);

        BigDecimal pay11 = BigDecimal.valueOf(300l);
        BigDecimal pay12 = BigDecimal.valueOf(450l);
        BigDecimal pay2 = BigDecimal.valueOf(4500l);

        //createTestSythAcc(Account.Behavior.A, "client_main");
        //createTestSythAcc(Account.Behavior.AP, "buffer");

        DepositPayInAccountDocument clientPayInAccountDocument1 = billingService.doDepositPayInAccountRequest(101l, acc1);
        DepositPayInAccountDocument clientPayInAccountDocument2 = billingService.doDepositPayInAccountRequest(201l, acc2);

        System.out.println(clientPayInAccountDocument1.toString());
        System.out.println(clientPayInAccountDocument2.toString());

        billingService.doDepositPayInAccountConfirm(clientPayInAccountDocument1);
        billingService.doDepositPayInAccountConfirm(clientPayInAccountDocument2);


        billingService.doClientPaymentForOrder(101l, 3l, pay11);
        billingService.doClientPaymentForOrder(201l, 4l, pay2);
        billingService.doClientPaymentForOrder(101l, 6l, pay12);




    }*/
}
