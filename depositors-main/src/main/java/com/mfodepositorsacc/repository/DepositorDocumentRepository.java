package com.mfodepositorsacc.repository;

import com.mfodepositorsacc.dmodel.DepositorDocument;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by berz on 18.11.2015.
 */
@Transactional(readOnly = true)
public interface DepositorDocumentRepository extends CrudRepository<DepositorDocument, Long>, JpaSpecificationExecutor {

}
