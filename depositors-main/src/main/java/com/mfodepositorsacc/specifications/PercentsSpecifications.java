package com.mfodepositorsacc.specifications;

import com.mfodepositorsacc.accountingmachine.utils.Assert;
import com.mfodepositorsacc.dmodel.DepositorTypeSettings;
import com.mfodepositorsacc.dmodel.Percents;
import com.mfodepositorsacc.dto.DepositCalculation;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by berz on 29.04.15.
 */
public class PercentsSpecifications {

    public static Specification<Percents> forDepositCalculation(final DepositCalculation depositCalculation){
        return new Specification<Percents>() {
            @Override
            public Predicate toPredicate(Root<Percents> percentsRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Assert.isNotNull(depositCalculation.sum);
                Assert.isNotNull(depositCalculation.length);
                Assert.isNotNull(depositCalculation.depositorFormType);

                Path<Integer> lengthFromPath = percentsRoot.get("lengthFrom");
                Path<Integer> lengthBySumSettings = percentsRoot.get("sumSettings").get("maxLength");

                // length между lengthFrom и максимально разрешенным в sumSettings
                Predicate lengthPredicate = criteriaBuilder.and(
                        criteriaBuilder.lessThanOrEqualTo(lengthFromPath, depositCalculation.length),
                        criteriaBuilder.greaterThanOrEqualTo(lengthBySumSettings, depositCalculation.length)
                );

                Path<BigDecimal> sumFromPath = percentsRoot.get("sumSettings").get("sumFrom");
                Path<BigDecimal> sumByDepositorTypeSettings = percentsRoot
                        .get("sumSettings")
                        .get("depositorTypeSettings")
                        .get("maxSum");

                // сумма между sumFrom и максимально допустимой суммой
                Predicate sumPredicate = criteriaBuilder.and(
                        criteriaBuilder.lessThanOrEqualTo(sumFromPath, depositCalculation.sum),
                        criteriaBuilder.greaterThanOrEqualTo(sumByDepositorTypeSettings, depositCalculation.sum)
                );

                Path<DepositorTypeSettings.DepositorFormType> depositorFormTypePath = percentsRoot
                        .get("sumSettings")
                        .get("depositorTypeSettings")
                        .get("depositorFormType");

                Predicate depositorFormTypePredicate = criteriaBuilder.equal(depositorFormTypePath, depositCalculation.depositorFormType);


                List<Order> orderList = new LinkedList<Order>();
                orderList.add(criteriaBuilder.desc(sumFromPath));
                orderList.add(criteriaBuilder.desc(lengthFromPath));

                return criteriaQuery.where(
                        criteriaBuilder.and(
                                lengthPredicate,
                                sumPredicate,
                                depositorFormTypePredicate
                        )
                )
                        .orderBy(new LinkedList<Order>(
                               orderList
                        ))
                        .getRestriction();
            }
        };
    }

}
