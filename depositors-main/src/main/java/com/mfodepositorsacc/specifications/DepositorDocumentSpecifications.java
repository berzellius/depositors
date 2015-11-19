package com.mfodepositorsacc.specifications;

import com.mfodepositorsacc.dmodel.Deposit;
import com.mfodepositorsacc.dmodel.DepositorDocument;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Created by berz on 18.11.2015.
 */
public class DepositorDocumentSpecifications {
    public static Specification<DepositorDocument> byType(final DepositorDocument.Type type){
        return new Specification<DepositorDocument>() {
            @Override
            public Predicate toPredicate(Root<DepositorDocument> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("type"), type);
            }
        };
    }

    public static Specification<DepositorDocument> byDeposit(final Deposit deposit){
        return new Specification<DepositorDocument>() {
            @Override
            public Predicate toPredicate(Root<DepositorDocument> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("deposit"), deposit);
            }
        };
    }

    public static Specification<DepositorDocument> notDeleted() {
        return new Specification<DepositorDocument>() {
            @Override
            public Predicate toPredicate(Root<DepositorDocument> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.notEqual(root.get("deleted"), true);
            }
        };
    }

    public static Specification<DepositorDocument> validated() {
        return new Specification<DepositorDocument>() {
            @Override
            public Predicate toPredicate(Root<DepositorDocument> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("validated"), true);
            }
        };
    }
}
