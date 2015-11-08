package com.mfodepositorsacc.repository;

import com.mfodepositorsacc.dmodel.ManagedGroupMemberRole;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by berz on 03.11.2015.
 */
@Transactional
public interface ManagedGroupMemberRoleRepository extends CrudRepository<ManagedGroupMemberRole, Long> {
}
