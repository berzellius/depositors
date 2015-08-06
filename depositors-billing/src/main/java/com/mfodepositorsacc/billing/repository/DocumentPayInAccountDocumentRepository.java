package com.mfodepositorsacc.billing.repository;

import com.mfodepositorsacc.billing.dmodel.DepositPayInAccountDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by berz on 18.01.15.
 */
@Transactional(readOnly = true)
public interface DocumentPayInAccountDocumentRepository extends CrudRepository<DepositPayInAccountDocument, Long>,JpaSpecificationExecutor {

    DepositPayInAccountDocument findByNum(String num);

    List<DepositPayInAccountDocument> findByDepositIdAndStateOrderByDtmCreateDesc(Long depositId, DepositPayInAccountDocument.State state, Pageable pageable);

    Page<DepositPayInAccountDocument> findByStateOrderByDtmCreateDesc(DepositPayInAccountDocument.State state, Pageable pageable);
}
