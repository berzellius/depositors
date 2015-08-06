package com.mfodepositorsacc.repository;

import com.mfodepositorsacc.dmodel.Region;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by berz on 07.05.15.
 */
@Transactional(readOnly = true)
public interface RegionRepository extends CrudRepository<Region, Long> {
}
