package com.mfodepositorsacc.service;

import com.mfodepositorsacc.dmodel.Deposit;
import com.mfodepositorsacc.dmodel.DepositorDocument;
import com.mfodepositorsacc.dmodel.UploadedFile;
import com.mfodepositorsacc.exceptions.UploadFileException;
import com.mfodepositorsacc.repository.DepositorDocumentRepository;
import com.mfodepositorsacc.specifications.DepositorDocumentSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * Created by berz on 17.05.15.
 */
@Service
@Transactional
public class DepositorServiceImpl implements DepositorService {

    @Autowired
    UploadedFileService uploadedFileService;

    @Autowired
    DepositorDocumentRepository depositorDocumentRepository;

    @Override
    public void addDepositorDocument(Deposit deposit, MultipartFile multipartFile, DepositorDocument.Type type) throws UploadFileException {
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setDtmCreate(new Date());
        uploadedFileService.uploadFile(uploadedFile, multipartFile);

        unvalidateAllDepositDocuments(deposit, type);

        DepositorDocument depositorDocument = new DepositorDocument();
        depositorDocument.setDtmCreate(new Date());
        depositorDocument.setDeposit(deposit);
        depositorDocument.setType(type);
        depositorDocument.setValidated(true);
        depositorDocument.setDeleted(false);
        depositorDocument.setUploadedFile(uploadedFile);

        depositorDocumentRepository.save(depositorDocument);
    }

    private void unvalidateAllDepositDocuments(Deposit deposit, DepositorDocument.Type type) {
        List<DepositorDocument> depositorDocuments = depositorDocumentsByTypeAndDeposit(deposit, type);

        for(DepositorDocument depositorDocument : depositorDocuments){
            depositorDocument.setValidated(false);
        }

        depositorDocumentRepository.save(depositorDocuments);
    }

    @Override
    public List<DepositorDocument> depositorDocumentsByTypeAndDeposit(Deposit deposit, DepositorDocument.Type type){
        return (List<DepositorDocument>) depositorDocumentRepository.findAll(
                Specifications.where(
                        DepositorDocumentSpecifications.byDeposit(deposit)
                )
                        .and(
                                DepositorDocumentSpecifications.byType(type)
                        )
                        .and(
                                DepositorDocumentSpecifications.notDeleted()
                        )
        );

    }

    @Override
    public void validateDepositorDocument(DepositorDocument depositorDocument) {
        unvalidateAllDepositDocuments(depositorDocument.getDeposit(), depositorDocument.getType());
        depositorDocument.setValidated(true);
        depositorDocumentRepository.save(depositorDocument);
    }

    @Override
    public void deleteDepositorDocument(DepositorDocument depositorDocument) {
        depositorDocument.setDeleted(true);
        depositorDocumentRepository.save(depositorDocument);
    }

    @Override
    public HashMap<DepositorDocument.Type, DepositorDocument> getDepositorDocumentsReady(Deposit deposit) {
        List<DepositorDocument> docs = depositorDocumentRepository.findAll(
                Specifications.where(
                        DepositorDocumentSpecifications.validated()
                )
                .and(
                        DepositorDocumentSpecifications.byDeposit(deposit)
                )
                .and(
                        DepositorDocumentSpecifications.notDeleted()
                )
        );

        HashMap<DepositorDocument.Type, DepositorDocument> result = new LinkedHashMap<DepositorDocument.Type, DepositorDocument>();

        for(DepositorDocument depositorDocument : docs){
            result.put(depositorDocument.getType(), depositorDocument);
        }

        return result;
    }
}
