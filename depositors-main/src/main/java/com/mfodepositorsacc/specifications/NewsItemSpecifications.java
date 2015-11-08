package com.mfodepositorsacc.specifications;

import com.mfodepositorsacc.dmodel.ManagedUnit;
import com.mfodepositorsacc.dmodel.NewsItem;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by berz on 03.11.2015.
 */
public class NewsItemSpecifications {
    public static Specification<NewsItem> byManagedUnit(final ManagedUnit managedUnit){
        return new Specification<NewsItem>() {
            @Override
            public Predicate toPredicate(Root<NewsItem> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Path<Set<ManagedUnit>> managedUnitsPath = root.get("managedUnits");
                return criteriaBuilder.isMember(managedUnit, managedUnitsPath);
            }
        };
    }

    public static Specification<NewsItem> byManagedUnits(final Set<ManagedUnit> managedUnits){
        return new Specification<NewsItem>() {
            @Override
            public Predicate toPredicate(Root<NewsItem> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new LinkedList<Predicate>();
                for(ManagedUnit managedUnit : managedUnits){
                    Predicate predicate = byManagedUnit(managedUnit).toPredicate(root, criteriaQuery, criteriaBuilder);
                    predicates.add(predicate);
                }
                return criteriaBuilder.or((Predicate[]) predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }

    public static Specification<NewsItem> published() {
        return new Specification<NewsItem>() {
            @Override
            public Predicate toPredicate(Root<NewsItem> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("published"), true);
            }
        };
    }

    public static Specification<NewsItem> nonDeleted(){
        return new Specification<NewsItem>() {
            @Override
            public Predicate toPredicate(Root<NewsItem> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.notEqual(root.get("deleted"), true);
            }
        };
    }
}
