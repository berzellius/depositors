package com.mfodepositorsacc.accountingmachine.service;

import com.mfodepositorsacc.accountingmachine.dmodel.Account;
import com.mfodepositorsacc.accountingmachine.dmodel.AccountHistory;
import com.mfodepositorsacc.accountingmachine.dmodel.AccountTransaction;
import com.mfodepositorsacc.accountingmachine.exception.RedSaldoException;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by berz on 14.01.15.
 */
public interface AccountMachine {
    Account seekAccount(LinkedHashMap<String, String> analytics);

    Account seekAccount(LinkedHashMap<String, String> analytics, boolean createIfNotExists);

    Account createAccount(Account acc);

    void doTransaction(AccountTransaction transaction) throws RedSaldoException;

    void doTransaction(AccountTransaction transaction, boolean callExceptionOnRedSaldo) throws RedSaldoException;

    void undoTransaction(AccountTransaction transaction) throws RedSaldoException;

    void undoTransaction(AccountTransaction transaction, boolean callExceptionOnRedSaldo) throws RedSaldoException;

    BigDecimal getSaldo(Account account, Date date);

    List<AccountHistory> getAccHistory(Account account, Date after);

    List<AccountHistory> getAccHistoryByMultipleAnalytics(List<LinkedHashMap<String,String>> analytics, Date after);

    void flushRedSaldo(Account acc) throws RedSaldoException;
}
