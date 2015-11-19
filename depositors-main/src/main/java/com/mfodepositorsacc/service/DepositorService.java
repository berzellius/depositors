package com.mfodepositorsacc.service;

import com.mfodepositorsacc.dmodel.Deposit;
import com.mfodepositorsacc.dmodel.DepositorDocument;
import com.mfodepositorsacc.exceptions.UploadFileException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by berz on 17.05.15.
 */
@Service
public interface DepositorService {


    void addDepositorDocument(Deposit deposit, MultipartFile multipartFile, DepositorDocument.Type type) throws UploadFileException;

    List<DepositorDocument> depositorDocumentsByTypeAndDeposit(Deposit deposit, DepositorDocument.Type type);

    void validateDepositorDocument(DepositorDocument depositorDocument);

    void deleteDepositorDocument(DepositorDocument depositorDocument);

    HashMap<DepositorDocument.Type, DepositorDocument> getDepositorDocumentsReady(Deposit deposit);
}
