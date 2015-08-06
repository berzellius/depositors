package com.mfodepositorsacc.specifications;

import com.mfodepositorsacc.dmodel.Depositor;
import com.mfodepositorsacc.dmodel.User;
import com.mfodepositorsacc.dmodel.UserRole;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.Set;

/**
 * Created by berz on 17.05.15.
 */
public class UserSpecifications {

    public static Specification<User> nonConfirmedRegistration(final UserRole userRole){
        return new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> userRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate rolePredicate = hasRole(userRole).toPredicate(userRoot, criteriaQuery, criteriaBuilder);
                Predicate enabledPredicate = criteriaBuilder.isFalse(userRoot.<Boolean>get("enabled"));
                Predicate activationCode = criteriaBuilder.isNotNull(userRoot.get("activationCode"));

                return criteriaBuilder.and(rolePredicate, enabledPredicate, activationCode);
            }
        };
    }


    public static Specification<User> hasRole(final UserRole role){
        return new Specification<User>() {

            @Override
            public Predicate toPredicate(Root<User> userRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Path<Set<UserRole>> userRolesPath = userRoot.get("authorities");

                return criteriaBuilder.isMember(role, userRolesPath);
            }
        };
    }

    public static Specification<User> byDepositor(final Depositor depositor){
        return new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> userRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Path<Depositor> depositorPath = userRoot.get("deposit").get("depositor");

                return criteriaBuilder.equal(depositorPath, depositor);
            }
        };
    }

}
