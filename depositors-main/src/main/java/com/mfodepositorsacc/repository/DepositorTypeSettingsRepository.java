package com.mfodepositorsacc.repository;

import com.mfodepositorsacc.dmodel.DepositorTypeSettings;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by berz on 27.04.15.
 */
@Transactional(readOnly = true)
public interface DepositorTypeSettingsRepository extends CrudRepository<DepositorTypeSettings, Long> {

    DepositorTypeSettings findOneByDepositorFormType(DepositorTypeSettings.DepositorFormType depositorFormType);

}
