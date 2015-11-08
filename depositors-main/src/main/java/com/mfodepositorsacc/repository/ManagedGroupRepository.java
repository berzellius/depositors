package com.mfodepositorsacc.repository;

import com.mfodepositorsacc.dmodel.ManagedGroup;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

/**
 * Created by berz on 03.11.2015.
 */
@Transactional
public interface ManagedGroupRepository extends CrudRepository<ManagedGroup, Long> {
}
