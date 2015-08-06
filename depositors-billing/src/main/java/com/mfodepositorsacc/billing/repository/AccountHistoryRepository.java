package com.mfodepositorsacc.billing.repository;

import com.mfodepositorsacc.accountingmachine.dmodel.Account;
import com.mfodepositorsacc.accountingmachine.dmodel.AccountHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by berz on 02.02.15.
 */
@Transactional(readOnly = true)
public interface AccountHistoryRepository extends CrudRepository<AccountHistory, Long> {

    Page<AccountHistory> findByAccount(Account account, Pageable pageable);

    Page<AccountHistory> findByAccountOrderByDtmUpdateDesc(Account account, Pageable pageable);
}
