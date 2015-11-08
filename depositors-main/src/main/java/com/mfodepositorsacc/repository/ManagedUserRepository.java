package com.mfodepositorsacc.repository;

import com.mfodepositorsacc.dmodel.ManagedUnit;
import com.mfodepositorsacc.dmodel.ManagedUser;
import com.mfodepositorsacc.dmodel.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by berz on 26.10.2015.
 */
@Transactional
public interface ManagedUserRepository extends CrudRepository<ManagedUser, Long> {
        public ManagedUser findByUser(User user);
}
