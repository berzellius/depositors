package com.mfodepositorsacc.billing.repository;

import com.mfodepositorsacc.billing.dmodel.DepositOutcomeDocument;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by berz on 21.06.15.
 */
@Transactional
public interface DepositOutcomeDocumentRepository extends CrudRepository<DepositOutcomeDocument, Long>, JpaSpecificationExecutor {
    DepositOutcomeDocument findByNum(String num);
}
