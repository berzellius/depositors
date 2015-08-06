package com.mfodepositorsacc.specifications;

import com.mfodepositorsacc.dmodel.Notification;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Created by berz on 04.06.15.
 */
public class NotificationSpecifications {

    public static Specification<Notification> waiting(){
        return new Specification<Notification>() {
            @Override
            public Predicate toPredicate(Root<Notification> notificationRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(notificationRoot.get("status"), Notification.Status.WAITING);
            }
        };
    }

    public static Specification<Notification> registrationConfirm(){
        return new Specification<Notification>() {
            @Override
            public Predicate toPredicate(Root<Notification> notificationRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(notificationRoot.get("template"), Notification.Template.PROFILE_ACTIVATION);
            }
        };
    }

    public static Specification<Notification> waitingRegistrationConfirm(){
        return new Specification<Notification>() {
            @Override
            public Predicate toPredicate(Root<Notification> notificationRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.and(
                        registrationConfirm().toPredicate(notificationRoot, criteriaQuery, criteriaBuilder),
                        waiting().toPredicate(notificationRoot, criteriaQuery, criteriaBuilder)
                );
            }
        };
    }

}
