package com.mfodepositorsacc.billing.service;

import com.mfodepositorsacc.accountingmachine.dmodel.Account;
import com.mfodepositorsacc.accountingmachine.dmodel.AccountHistory;

import java.util.Date;
import java.util.List;

/**
 * Created by berz on 17.01.15.
 */
public interface AccountUtils {

    Account getDepositAccount(Long depositId);

    Account getDepositSubAccountForOutcome(Long depositId);

    Account getDepositInitialSubAccount(Long depositId);

    Account getBufferAccount();

    List<AccountHistory> getDepositAccHistory(Long depositId, Date after);

    List<AccountHistory> getDepositAvailableAccHistory(Long depositId, Date after);
}
