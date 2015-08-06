package com.mfodepositorsacc.accountingmachine.service;

import com.mfodepositorsacc.accountingmachine.dmodel.Account;
import com.mfodepositorsacc.accountingmachine.dmodel.AccountHistory;
import com.mfodepositorsacc.accountingmachine.dmodel.AccountTransaction;
import com.mfodepositorsacc.accountingmachine.dmodel.StateHistory;
import com.mfodepositorsacc.accountingmachine.exception.RedSaldoException;
import com.mfodepositorsacc.accountingmachine.utils.Assert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.zip.CRC32;

/**
 * Created by berz on 14.01.15.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AccountMachineImpl implements AccountMachine {

    @PersistenceContext
    EntityManager entityManager;

    private Account getAnalyticAccBySynthAndAnalytics(Account syntheticAcc, LinkedHashMap<String, String> analytics) {
        Account analyticAcc = new Account();
        for (String analytic : analytics.keySet()) {
            if (analytic.equals("code")) {
                analyticAcc.setCode(analytics.get(analytic));
            }
            if (analytic.equals("idDeposit")) {
                Integer id = Integer.parseInt(analytics.get(analytic));
                analyticAcc.setIdDeposit((id != null) ? id : 0);

            }

        }


        // Проверяем, нет ли null аналитик. Если такие есть, меняем null на 0.
        // TODO: Внедрить в проект список актуальных аналитик, по которому сверяться.


        analyticAcc.setSchema(syntheticAcc.getSchema());


        analyticAcc.setBehavior(syntheticAcc.getBehavior());
        analyticAcc.setSynthetic(false);

        analyticAcc.setSaldo(BigDecimal.ZERO);
        analyticAcc.setRedSaldo(false);
        analyticAcc.setSchema(syntheticAcc.getSchema());

        return analyticAcc;
    }

    @Override
    public Account seekAccount(LinkedHashMap<String, String> analytics) {
        return seekAccount(analytics, true);
    }

    @Override
    public Account seekAccount(LinkedHashMap<String, String> analytics, boolean createIfNotExists) {


        Assert.isNotNull(analytics, "Null analytics given for seek. Cant find anything");

        // Получаем crc32 сумму строкового представления LinkedHashMap аналитик
        byte[] b = analytics.toString().getBytes();
        CRC32 crc = new CRC32();
        crc.update(b);
        Long crc32 = crc.getValue();

        String code = analytics.get("code");
        Assert.isNotNull(code, "Внутренняя ошибка. Не передана служебная аналитика code");


        // Достаем синтетический счет по коду

        /*Account syntheticAcc = (Account) sessionFactory.getCurrentSession()
                .createCriteria(Account.class)
                .add(Restrictions.eq("code", code))
                .add(Restrictions.eq("isSynthetic", true))
                .uniqueResult();
        */


        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Account> criteriaQuery = builder.createQuery(Account.class);
        Root<Account> accountRoot = criteriaQuery.from(Account.class);
        Predicate syntheticAccPredicate = builder.and(
            builder.equal(accountRoot.get("code"), code),
                builder.equal(accountRoot.get("isSynthetic"), true)
        );
        criteriaQuery.where(syntheticAccPredicate);
        Query syntheticAccQuery = entityManager.createQuery(criteriaQuery);

        Account syntheticAcc = null;

        Assert.isTrue(
                !syntheticAccQuery.getResultList().isEmpty(),
                "Не найден синтетический аккаунт по следующим аналитикам " + analytics.toString()
        );

        syntheticAcc = (Account) syntheticAccQuery.getSingleResult();
        // Устанавливаем блокировку Advisory на уровне транзакции
        // Ключом служит crc32 сумма строкового представления LinkedHashMap набора аналитик
        if (createIfNotExists) {
            entityManager.createNativeQuery("SELECT 1 FROM pg_advisory_xact_lock(" + crc32.toString() + ")").getSingleResult();
        }
        // TODO: валидация схемы счета

        // Находим аналитический счет.
        Predicate analyticAccPredicate = builder.equal(builder.literal(1), 1);
        CriteriaQuery<Account> analyticAccQuery = builder.createQuery(Account.class);
        Root<Account> analyticAccRoot = analyticAccQuery.from(Account.class);
        for (String analytic : analytics.keySet()) {
            String analyticStr = analytics.get(analytic);
            analyticAccPredicate = builder.and(analyticAccPredicate, builder.equal(analyticAccRoot.get(analytic), analyticStr));
        }
        analyticAccQuery.where(analyticAccPredicate);
        Query analyticQuery = entityManager.createQuery(analyticAccQuery);


        Account analyticAcc = null;

        if(analyticQuery.getResultList().isEmpty()){
            if(createIfNotExists)
                return createAccount(getAnalyticAccBySynthAndAnalytics(syntheticAcc, analytics));
            else return null;
        }

        if(analyticQuery.getResultList().size() > 1){
            throw new IllegalArgumentException("Внутренняя ошибка. Счет по набору аналитик " + analytics + " существует в нескольких экземплярах.");
        }

        analyticAcc = (Account) entityManager.createQuery(analyticAccQuery).getSingleResult();


        // TODO: проверка что вернулось не два счета

        return analyticAcc;
    }

    @Override
    public Account createAccount(Account acc) {
        if (acc.isSynthetic()) {
            throw new UnsupportedOperationException("Cant create Synthetic accounts. Please use accountingMachine.CreateAccount() function for creating analytic accounts only");
        }

        if (acc.getBehavior() == null) acc.setBehavior(Account.Behavior.A);

        if (acc.getSaldo() == null) acc.setSaldo(BigDecimal.ZERO);


        if (acc.getCode() == null)
            throw new RuntimeException("Cant create  accounts without code def. Please determine code for account and set need analytics");
        // TODO: рабочая валидация схемы счета
        Date dt = new Date();

        acc.setDtmCreate(dt);
        acc.setDtmUpdate(dt);

        entityManager.persist(acc);
        entityManager.flush();
        entityManager.refresh(acc);

        return acc;
    }

    @Override
    public void doTransaction(AccountTransaction accounttransaction) throws RedSaldoException {
        doTransaction(accounttransaction, true);
    }

    @Override
    public void doTransaction(AccountTransaction accounttransaction, boolean callExceptionOnRedSaldo) throws RedSaldoException {
        Assert.isTrue(accounttransaction.getState() == AccountTransaction.State.NEW, "Ошибка состояния транзакции. Выполнить можно только транзакцию в состоянии NEW!");
        Assert.isNotNull(accounttransaction.getSum(), "Сумма транзакции не может быть пустой.");
        Assert.isNotNull(accounttransaction.getAccountCt());
        Assert.isNotNull(accounttransaction.getAccountDt());

        Account accountCt = accounttransaction.getAccountCt();
        Account accountDt = accounttransaction.getAccountDt();

        Assert.isNotNull(accountCt);
        Assert.isNotNull(accountDt);

        // Дабы избежать дедлоков, отсортируем по hashCode
        if (accountCt.hashCode() < accountDt.hashCode()) {
            entityManager.refresh(accountCt, LockModeType.PESSIMISTIC_WRITE);
            entityManager.refresh(accountDt, LockModeType.PESSIMISTIC_WRITE);

        } else {
            entityManager.refresh(accountDt, LockModeType.PESSIMISTIC_WRITE);
            entityManager.refresh(accountCt, LockModeType.PESSIMISTIC_WRITE);
        }

        Date currentDate = new Date();
        accounttransaction.setDtmUpdate(currentDate);
        if (accounttransaction.getDtmCreate() == null) {
            accounttransaction.setDtmCreate(currentDate);
        }


        accountCt.setDtmUpdate(currentDate);
        accountDt.setDtmUpdate(currentDate);

        Account.Behavior behaviorCt = accountCt.getBehavior();
        Account.Behavior behaviorDt = accountDt.getBehavior();

        // Перевод средств
        accountCt.setSaldo(accountCt.getSaldo().subtract(accounttransaction.getSum()));
        accountDt.setSaldo(accountDt.getSaldo().add(accounttransaction.getSum()));

        AccountHistory ah1 = new AccountHistory();
        ah1.setFields(accountCt, accounttransaction, currentDate);

        AccountHistory ah = new AccountHistory();
        ah.setFields(accountDt, accounttransaction, currentDate);

        // Проверка на красное сальдо
        if ((accountCt.getSaldo().compareTo(BigDecimal.ZERO) == -1 && behaviorCt == Account.Behavior.A)
                || (accountCt.getSaldo().compareTo(BigDecimal.ZERO) == 1 && behaviorCt == Account.Behavior.P)) {
            if (callExceptionOnRedSaldo) {
                throw new RedSaldoException(accountCt, accounttransaction);
            } else {
                accountCt.setRedSaldo(true);
            }
        }

        if ((accountDt.getSaldo().compareTo(BigDecimal.ZERO) == -1 && behaviorDt == Account.Behavior.A)
                || (accountDt.getSaldo().compareTo(BigDecimal.ZERO) == 1 && behaviorDt == Account.Behavior.P)) {
            if (callExceptionOnRedSaldo) {
                throw new RedSaldoException(accountDt, accounttransaction);
            } else {
                accountDt.setRedSaldo(true);
            }
        }

        accounttransaction.setState(AccountTransaction.State.DONE);

        entityManager.merge(accountCt);
        entityManager.merge(accountDt);

        entityManager.persist(accounttransaction);

        entityManager.persist(ah);
        entityManager.persist(ah1);

        entityManager.flush();

        entityManager.refresh(accounttransaction);


        StateHistory atsh = new StateHistory(
                AccountTransaction.class.toString(),
                accounttransaction.getId(),
                AccountTransaction.State.NEW.toString(),
                AccountTransaction.State.DONE.toString()
        );

        entityManager.persist(atsh);
    }

    @Override
    public void undoTransaction(AccountTransaction accounttransaction) throws RedSaldoException {
        undoTransaction(accounttransaction, true);
    }

    @Override
    public void undoTransaction(AccountTransaction accounttransaction, boolean callExceptionOnRedSaldo) throws RedSaldoException {
        Assert.isTrue(
                accounttransaction.getState() == AccountTransaction.State.DONE, "Отменить транзакцию можно только в состоянии Выполнена"
        );


        Date currentDate = new Date();
        accounttransaction.setDtmUpdate(currentDate);



        Account accountCt = accounttransaction.getAccountCt();
        Account accountDt = accounttransaction.getAccountDt();


        // Дабы избежать дедлоков, отсортируем по hashCode
        if (accountCt.hashCode() < accountDt.hashCode()) {
            entityManager.lock(accountCt, LockModeType.PESSIMISTIC_WRITE);
            entityManager.lock(accountDt, LockModeType.PESSIMISTIC_WRITE);

        } else {
            entityManager.lock(accountDt, LockModeType.PESSIMISTIC_WRITE);
            entityManager.lock(accountCt, LockModeType.PESSIMISTIC_WRITE);

        }

        accountCt.setDtmUpdate(currentDate);
        accountDt.setDtmUpdate(currentDate);

        Account.Behavior behaviorCt = accountCt.getBehavior();
        Account.Behavior behaviorDt = accountDt.getBehavior();

        // Возврат средств
        accountCt.setSaldo(accountCt.getSaldo().add(accounttransaction.getSum()));
        accountDt.setSaldo(accountDt.getSaldo().subtract(accounttransaction.getSum()));

        AccountHistory ah1 = new AccountHistory();
        ah1.setFields(accountCt, accounttransaction, currentDate);

        AccountHistory ah = new AccountHistory();
        ah.setFields(accountDt, accounttransaction, currentDate);

        // Проверка на красное сальдо
        if ((accountCt.getSaldo().compareTo(BigDecimal.ZERO) == -1 && behaviorCt == Account.Behavior.A)
                || (accountCt.getSaldo().compareTo(BigDecimal.ZERO) == 1 && behaviorCt == Account.Behavior.P)) {
            if (callExceptionOnRedSaldo) throw new RedSaldoException(accountCt);
            else accountCt.setRedSaldo(true);
        }

        if ((accountDt.getSaldo().compareTo(BigDecimal.ZERO) == -1 && behaviorDt == Account.Behavior.A)
                || (accountDt.getSaldo().compareTo(BigDecimal.ZERO) == 1 && behaviorDt == Account.Behavior.P)) {
            if (callExceptionOnRedSaldo) throw new RedSaldoException(accountDt);
            else accountDt.setRedSaldo(true);
        }

        accounttransaction.setState(AccountTransaction.State.CANCELED);


        entityManager.merge(accountCt);
        entityManager.merge(accountDt);

        entityManager.persist(accounttransaction);

        entityManager.persist(ah);
        entityManager.persist(ah1);

        entityManager.refresh(accounttransaction);


        StateHistory atsh = new StateHistory(
                AccountTransaction.class.toString(),
                accounttransaction.getId(),
                AccountTransaction.State.DONE.toString(),
                AccountTransaction.State.CANCELED.toString()
        );

        entityManager.persist(atsh);
    }

    @Override
    public BigDecimal getSaldo(Account account, Date date) {
        if (date == null) {
            Account acc = entityManager.find(Account.class, account.getId());
            if (acc != null) {
                return acc.getSaldo();
            } else {
                return BigDecimal.ZERO;
            }
        } else {
            //throw new UnsupportedOperationException("Запрос состояния счета на дату ранее текущей не реализован.");

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.HOUR_OF_DAY, 12);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);

            Date d = cal.getTime();


            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<AccountHistory> criteriaQuery = builder.createQuery(AccountHistory.class);
            Root<AccountHistory> accountHistoryRoot = criteriaQuery.from(AccountHistory.class);

            Path<Date> dateCreatedPath = accountHistoryRoot.get("dtmUpdate");

            Predicate findInHistory = builder.and(
                builder.lessThanOrEqualTo(dateCreatedPath, d),
                    builder.equal(accountHistoryRoot.get("account"), account)
            );

            criteriaQuery.where(findInHistory);
            criteriaQuery.orderBy(builder.desc(accountHistoryRoot.get("dtmUpdate")));

            List l = entityManager.createQuery(criteriaQuery).setFirstResult(0).setMaxResults(1).getResultList();


            Iterator<AccountHistory> i = l.iterator();

            if (i.hasNext()) {
                AccountHistory ah = i.next();
                return ah.getSaldo();
            } else {
                return BigDecimal.ZERO;
            }
        }
    }

    @Override
    public List<AccountHistory> getAccHistory(Account account, Date after){
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<AccountHistory> criteriaQuery = builder.createQuery(AccountHistory.class);
        Root<AccountHistory> accountHistoryRoot = criteriaQuery.from(AccountHistory.class);

        Path<Date> dateCreatedPath = accountHistoryRoot.get("dtmUpdate");
        Path<Date> dtAccPath = accountHistoryRoot.get("accountTransaction").get("dtAcc");

        List<Order> orderList = new LinkedList<Order>();
        orderList.add(builder.asc(dtAccPath));

        Predicate findInHistory = builder.and(
                builder.greaterThanOrEqualTo(dateCreatedPath, after),
                builder.equal(accountHistoryRoot.get("account"), account)
        );

        criteriaQuery.where(findInHistory);
        criteriaQuery.orderBy(orderList);

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public List<AccountHistory> getAccHistoryByMultipleAnalytics(List<LinkedHashMap<String, String>> analytics, Date after) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<AccountHistory> criteriaQuery = builder.createQuery(AccountHistory.class);
        Root<AccountHistory> accountHistoryRoot = criteriaQuery.from(AccountHistory.class);

        Path<Date> dateCreatedPath = accountHistoryRoot.get("dtmUpdate");
        Path<Date> dtAccPath = accountHistoryRoot.get("accountTransaction").get("dtAcc");

        List<Order> orderList = new LinkedList<Order>();
        orderList.add(builder.asc(dtAccPath));

        Predicate multipleAnalytics = builder.equal(builder.literal(1), 0);
        for(LinkedHashMap<String, String> analytic : analytics){
            Predicate analyticCheck = builder.equal(builder.literal(1), 1);
            for (String an : analytic.keySet()) {
                String analyticStr = analytic.get(an);
                analyticCheck = builder.and(analyticCheck, builder.equal(accountHistoryRoot.get(an), analyticStr));
            }

            multipleAnalytics = builder.or(multipleAnalytics, analyticCheck);
        }

        Predicate findInHistory = builder.and(
                builder.greaterThanOrEqualTo(dateCreatedPath, after),
                multipleAnalytics
        );

        criteriaQuery.where(findInHistory);
        criteriaQuery.orderBy(orderList);

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public void flushRedSaldo(Account acc) throws RedSaldoException {
        if (acc.isRedSaldo()) {
            throw new RedSaldoException(acc);
        }
    }
}
