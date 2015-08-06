package com.mfodepositorsacc.billing.specifications;

import com.mfodepositorsacc.billing.dmodel.DepositOutcomeDocument;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by berz on 21.06.15.
 */
public class DocumentOutcomeSpecification {

    public static Specification<DepositOutcomeDocument> closing(final Long depositId){
        return new Specification<DepositOutcomeDocument>() {
            @Override
            public Predicate toPredicate(Root<DepositOutcomeDocument> depositOutcomeDocumentRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate typeClosing = criteriaBuilder.equal(depositOutcomeDocumentRoot.get("type"), DepositOutcomeDocument.Type.CLOSING);
                Predicate depositIdMatch = criteriaBuilder.equal(depositOutcomeDocumentRoot.get("depositId"), depositId);
                Predicate state = criteriaBuilder.equal(depositOutcomeDocumentRoot.get("state"), DepositOutcomeDocument.State.NEW);

                return criteriaBuilder.and(typeClosing, depositIdMatch, state);
            }
        };
    }

    public static Specification<DepositOutcomeDocument> byDepositId(final Long depositId) {
        return new Specification<DepositOutcomeDocument>() {
            @Override
            public Predicate toPredicate(Root<DepositOutcomeDocument> objectRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(objectRoot.get("depositId"), depositId);
            }
        };
    }

    public static Specification<DepositOutcomeDocument> orderByDateDesc() {
        return new Specification<DepositOutcomeDocument>() {
            @Override
            public Predicate toPredicate(Root<DepositOutcomeDocument> objectRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Path<Date> dtAcc = objectRoot.get("dtAcc");

                List<Order>  orderList = new LinkedList<Order>();
                orderList.add(criteriaBuilder.desc(dtAcc));

                return criteriaQuery.orderBy(orderList).getRestriction();
            }
        };
    }

    public static Specification<DepositOutcomeDocument> stateNew() {
        return new Specification<DepositOutcomeDocument>() {
            @Override
            public Predicate toPredicate(Root<DepositOutcomeDocument> depositOutcomeDocumentRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(depositOutcomeDocumentRoot.get("state"), DepositOutcomeDocument.State.NEW);
            }
        };
    }

    public static Specification<DepositOutcomeDocument> early() {
        return new Specification<DepositOutcomeDocument>() {
            @Override
            public Predicate toPredicate(Root<DepositOutcomeDocument> depositOutcomeDocumentRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(depositOutcomeDocumentRoot.get("type"), DepositOutcomeDocument.Type.EARLY);
            }
        };
    }

    public static Specification<DepositOutcomeDocument> done() {
        return new Specification<DepositOutcomeDocument>() {
            @Override
            public Predicate toPredicate(Root<DepositOutcomeDocument> depositOutcomeDocumentRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(depositOutcomeDocumentRoot.get("state"), DepositOutcomeDocument.State.DONE);
            }
        };
    }
}
