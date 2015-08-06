package com.mfodepositorsacc.repository;

import com.mfodepositorsacc.dmodel.Notification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by berz on 04.06.15.
 */
@Transactional(readOnly = true)
public interface NotificationRepository extends CrudRepository<Notification, Long>, JpaSpecificationExecutor {
}
