package com.mfodepositorsacc.repository;

import com.mfodepositorsacc.dmodel.SumSettings;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by berz on 28.05.15.
 */
@Transactional(readOnly = true)
public interface SumSettingsRepository extends CrudRepository<SumSettings, Long>, JpaSpecificationExecutor {

}
