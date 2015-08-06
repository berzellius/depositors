package com.mfodepositorsacc.billing.specifications;

import com.mfodepositorsacc.billing.dmodel.DepositPayMonthlyProfitDocument;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by berz on 27.06.15.
 */
public class DocumentPayMonthlyProfitSpecification {

    public static Specification<DepositPayMonthlyProfitDocument> byDepositId(final Long depositId){
        return new Specification<DepositPayMonthlyProfitDocument>() {
            @Override
            public Predicate toPredicate(Root<DepositPayMonthlyProfitDocument> depositPayMonthlyProfitDocumentRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(depositPayMonthlyProfitDocumentRoot.get("depositId"), depositId);
            }
        };
    }

    public static Specification<DepositPayMonthlyProfitDocument> orderByDate(){
        return new Specification<DepositPayMonthlyProfitDocument>() {
            @Override
            public Predicate toPredicate(Root<DepositPayMonthlyProfitDocument> depositPayMonthlyProfitDocumentRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Path<Date> dtAcc = depositPayMonthlyProfitDocumentRoot.get("dtAcc");

                List<Order> orderList = new LinkedList<Order>();
                orderList.add(criteriaBuilder.desc(dtAcc));

                return criteriaQuery.orderBy(orderList).getRestriction();
            }
        };
    }

    public static Specification<DepositPayMonthlyProfitDocument> done() {
        return new Specification<DepositPayMonthlyProfitDocument>() {
            @Override
            public Predicate toPredicate(Root<DepositPayMonthlyProfitDocument> depositPayMonthlyProfitDocumentRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(depositPayMonthlyProfitDocumentRoot.get("state"), DepositPayMonthlyProfitDocument.State.DONE);
            }
        };
    }
}
