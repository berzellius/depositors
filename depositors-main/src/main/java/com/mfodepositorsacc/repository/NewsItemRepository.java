package com.mfodepositorsacc.repository;

import com.mfodepositorsacc.dmodel.NewsItem;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by berz on 26.10.2015.
 */
@Transactional
public interface NewsItemRepository extends CrudRepository<NewsItem, Long>, JpaSpecificationExecutor {

}
