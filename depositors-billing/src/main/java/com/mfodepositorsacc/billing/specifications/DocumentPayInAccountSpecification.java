package com.mfodepositorsacc.billing.specifications;

import com.mfodepositorsacc.billing.dmodel.DepositPayInAccountDocument;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by berz on 21.06.15.
 */
public class DocumentPayInAccountSpecification {

    public static Specification<DepositPayInAccountDocument> newBase(final Long depositId){
        return new Specification<DepositPayInAccountDocument>() {
            @Override
            public Predicate toPredicate(Root<DepositPayInAccountDocument> documentPayInAccountSpecificationRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate typeBase = criteriaBuilder.equal(documentPayInAccountSpecificationRoot.get("type"), DepositPayInAccountDocument.Type.BASE);
                Predicate depositIdMatch = criteriaBuilder.equal(documentPayInAccountSpecificationRoot.get("depositId"), depositId);
                Predicate state = criteriaBuilder.equal(documentPayInAccountSpecificationRoot.get("state"), DepositPayInAccountDocument.State.NEW);

                return criteriaBuilder.and(typeBase, depositIdMatch, state);
            }
        };
    }

    public static Specification<DepositPayInAccountDocument> newBase(){
        return new Specification<DepositPayInAccountDocument>() {
            @Override
            public Predicate toPredicate(Root<DepositPayInAccountDocument> depositPayInAccountDocumentRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate typeBase = criteriaBuilder.equal(depositPayInAccountDocumentRoot.get("type"), DepositPayInAccountDocument.Type.BASE);
                Predicate state = criteriaBuilder.equal(depositPayInAccountDocumentRoot.get("state"), DepositPayInAccountDocument.State.NEW);

                return criteriaBuilder.and(typeBase, state);
            }
        };
    }

    public static Specification<DepositPayInAccountDocument> byDepositId(final Long depositId) {
        return new Specification<DepositPayInAccountDocument>() {
            @Override
            public Predicate toPredicate(Root<DepositPayInAccountDocument> depositPayInAccountDocumentRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(depositPayInAccountDocumentRoot.get("depositId"), depositId);
            }
        };
    }

    public static Specification<DepositPayInAccountDocument> done() {
        return new Specification<DepositPayInAccountDocument>() {
            @Override
            public Predicate toPredicate(Root<DepositPayInAccountDocument> depositPayInAccountDocumentRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(depositPayInAccountDocumentRoot.get("state"), DepositPayInAccountDocument.State.DONE);
            }
        };
    }

    public static Specification<DepositPayInAccountDocument> orderByDate(){
        return new Specification<DepositPayInAccountDocument>() {
            @Override
            public Predicate toPredicate(Root<DepositPayInAccountDocument> depositPayInAccountDocumentRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Path<Date> dtAcc = depositPayInAccountDocumentRoot.get("dtAcc");

                List<Order> orderList = new LinkedList<Order>();
                orderList.add(criteriaBuilder.desc(dtAcc));

                return criteriaQuery.orderBy(orderList).getRestriction();
            }
        };
    }

    public static Specification<DepositPayInAccountDocument> newItems() {
        return new Specification<DepositPayInAccountDocument>() {
            @Override
            public Predicate toPredicate(Root<DepositPayInAccountDocument> depositPayInAccountDocumentRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(depositPayInAccountDocumentRoot.get("state"), DepositPayInAccountDocument.State.NEW);
            }
        };
    }

    public static Specification<DepositPayInAccountDocument> additional() {
        return new Specification<DepositPayInAccountDocument>() {
            @Override
            public Predicate toPredicate(Root<DepositPayInAccountDocument> depositPayInAccountDocumentRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(depositPayInAccountDocumentRoot.get("type"), DepositPayInAccountDocument.Type.ADDITIONAL);
            }
        };
    }
}
