package com.mfodepositorsacc.repository;

import com.mfodepositorsacc.dmodel.Deposit;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by berz on 04.05.15.
 */
@Transactional(readOnly = true)
public interface DepositRepository extends CrudRepository<Deposit, Long>, JpaSpecificationExecutor {
}
