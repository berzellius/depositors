package com.mfodepositorsacc.billing.repository;

import com.mfodepositorsacc.billing.dmodel.DepositPayMonthlyProfitDocument;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by berz on 27.06.15.
 */
@Transactional(readOnly = true)
public interface DepositPayMonthlyProfitDocumentRepository extends CrudRepository<DepositPayMonthlyProfitDocument, Long>, JpaSpecificationExecutor {

}
