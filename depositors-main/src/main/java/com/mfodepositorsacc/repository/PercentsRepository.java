package com.mfodepositorsacc.repository;

import com.mfodepositorsacc.dmodel.Percents;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by berz on 29.04.15.
 */
@Transactional(readOnly = true)
public interface PercentsRepository extends CrudRepository<Percents, Long>, JpaSpecificationExecutor {
}
