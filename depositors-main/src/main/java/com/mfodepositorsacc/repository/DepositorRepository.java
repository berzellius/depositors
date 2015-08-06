package com.mfodepositorsacc.repository;

import com.mfodepositorsacc.dmodel.Depositor;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by berz on 12.05.15.
 */
@Transactional(readOnly = true)
public interface DepositorRepository extends CrudRepository<Depositor, Long>, JpaSpecificationExecutor {

}
