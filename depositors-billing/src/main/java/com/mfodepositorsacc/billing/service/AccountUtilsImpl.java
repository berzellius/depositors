package com.mfodepositorsacc.billing.service;

import com.mfodepositorsacc.accountingmachine.dmodel.Account;
import com.mfodepositorsacc.accountingmachine.dmodel.AccountHistory;
import com.mfodepositorsacc.accountingmachine.service.AccountMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by berz on 17.01.15.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AccountUtilsImpl implements AccountUtils {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    AccountMachine accountMachine;


    @Override
    public Account getDepositAccount(Long depositId) {
        LinkedHashMap<String, String> analytic = new LinkedHashMap<String, String>();
        analytic.put("code", "deposit_main");
        analytic.put("idDeposit", depositId.toString());

        return accountMachine.seekAccount(analytic);
    }

    @Override
    public Account getDepositSubAccountForOutcome(Long depositId) {
        LinkedHashMap<String, String> analytic = new LinkedHashMap<String, String>();
        analytic.put("code", "deposit_outcome_sub");
        analytic.put("idDeposit", depositId.toString());

        return accountMachine.seekAccount(analytic);
    }

    @Override
    public Account getDepositInitialSubAccount(Long depositId){
        LinkedHashMap<String, String> analytic = new LinkedHashMap<String, String>();
        analytic.put("code", "deposit_initial_sub");
        analytic.put("idDeposit", depositId.toString());

        return accountMachine.seekAccount(analytic);
    }

    @Override
    public Account getBufferAccount() {
        LinkedHashMap<String, String> analytic = new LinkedHashMap<String, String>();
        analytic.put("code", "buffer");

        return accountMachine.seekAccount(analytic);
    }

    @Override
    public List<AccountHistory> getDepositAccHistory(Long depositId, Date after){
        LinkedList<LinkedHashMap<String, String>> analytics = new LinkedList<LinkedHashMap<String, String>>();

        LinkedHashMap<String, String> analytics1 = new LinkedHashMap<String, String>();
        analytics1.put("code", "deposit_main");
        analytics1.put("idDeposit", depositId.toString());

        analytics.add(analytics1);

        LinkedHashMap<String, String> analytics2 = new LinkedHashMap<String, String>();
        analytics2.put("code", "deposit_initial_sub");
        analytics2.put("idDeposit", depositId.toString());

        analytics.add(analytics2);

        return accountMachine.getAccHistoryByMultipleAnalytics(analytics, after);
    }

    @Override
    public List<AccountHistory> getDepositAvailableAccHistory(Long depositId, Date after) {
        Account account = getDepositAccount(depositId);

        if(account == null) return null;

        return accountMachine.getAccHistory(account, after);
    }
}
