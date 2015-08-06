package com.mfodepositorsacc.specifications;

import com.mfodepositorsacc.dmodel.Percents;
import com.mfodepositorsacc.dmodel.SumSettings;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

/**
 * Created by berz on 31.05.15.
 */
public class SumSettingsSpecifications {

    public static Specification<SumSettings> nonConsistent(){
        return new Specification<SumSettings>() {
            @Override
            public Predicate toPredicate(Root<SumSettings> sumSettingsRoot, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Subquery<Percents> percentsSubquery = criteriaQuery.subquery(Percents.class);
                Root<Percents> percentsRoot = percentsSubquery.from(Percents.class);
                percentsSubquery.select(percentsRoot);
                percentsSubquery.where(
                        criteriaBuilder.and(
                                criteriaBuilder.equal(percentsRoot.get("lengthFrom"), sumSettingsRoot.get("minLength")),
                                criteriaBuilder.equal(percentsRoot.get("sumSettings"), sumSettingsRoot)
                        )

                );

                return criteriaBuilder.not(criteriaBuilder.exists(percentsSubquery));
            }
        };
    }

}
