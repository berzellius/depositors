package com.mfodepositorsacc.repository;

import com.mfodepositorsacc.dmodel.Deposit;
import com.mfodepositorsacc.dmodel.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by berz on 08.11.14.
 */
@Transactional(readOnly = true)
public interface UserRepository extends CrudRepository<User, Long>, JpaSpecificationExecutor {

    Page<User> findAllByDeposit(Deposit deposit, Pageable pageable);

    User findByDeposit(Deposit deposit);

    User findByUsername(String username);

    User findByActivationCode(String activationCode);

    Long countByEnabledAndActivationCode(Boolean enabled, String activationCode);
}
