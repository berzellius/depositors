package com.mfodepositorsacc.billing.service;

import com.mfodepositorsacc.accountingmachine.dmodel.Document;
import com.mfodepositorsacc.accountingmachine.exception.RedSaldoException;
import com.mfodepositorsacc.billing.dmodel.DepositOutcomeDocument;
import com.mfodepositorsacc.billing.dmodel.DepositPayInAccountDocument;
import com.mfodepositorsacc.billing.exception.DocumentAlreadyProcessingException;
import com.mfodepositorsacc.billing.exception.DocumentNotFoundException;
import com.mfodepositorsacc.billing.exception.IllegalDocumentStateException;
import com.mfodepositorsacc.billing.repository.DepositOutcomeDocumentRepository;
import com.mfodepositorsacc.billing.repository.DepositPayMonthlyProfitDocumentRepository;
import com.mfodepositorsacc.billing.repository.DocumentPayInAccountDocumentRepository;
import com.mfodepositorsacc.billing.specifications.DocumentOutcomeSpecification;
import com.mfodepositorsacc.billing.specifications.DocumentPayInAccountSpecification;
import com.mfodepositorsacc.billing.specifications.DocumentPayMonthlyProfitSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by berz on 18.01.15.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DocumentsServiceImpl implements DocumentsService {

    @Autowired
    DocumentPayInAccountDocumentRepository documentPayInAccountDocumentRepository;

    @Autowired
    DepositPayMonthlyProfitDocumentRepository depositPayMonthlyProfitDocumentRepository;

    @Autowired
    DepositOutcomeDocumentRepository depositOutcomeDocumentRepository;

    @Autowired
    BillingService billingService;

    @Override
    public List<DepositPayInAccountDocument> getClientPayInDocumentsActual(Long id) {
        PageRequest pageRequest = new PageRequest(0, 10);
        return documentPayInAccountDocumentRepository.findByDepositIdAndStateOrderByDtmCreateDesc(id, DepositPayInAccountDocument.State.NEW, pageRequest);
    }

    @Override
    public Page<DepositPayInAccountDocument> getClientsPayInDocumentsActual(Pageable pageable) {
        return documentPayInAccountDocumentRepository.findByStateOrderByDtmCreateDesc(DepositPayInAccountDocument.State.NEW, pageable);
    }

    @Override
    public Long countNewBasePayInRequests() {
        return documentPayInAccountDocumentRepository.count(DocumentPayInAccountSpecification.newBase());
    }

    @Override
    public Long countNewAdditionalPayInRequests() {
        return documentPayInAccountDocumentRepository.count(
                Specifications.where(
                        DocumentPayInAccountSpecification.newItems()
                )
                        .and(DocumentPayInAccountSpecification.additional())
        );
    }

    @Override
    public Long countNewEarlyOutcomeRequests() {
        return depositOutcomeDocumentRepository.count(
                Specifications.where(
                        DocumentOutcomeSpecification.stateNew()
                )
                .and(DocumentOutcomeSpecification.early())
        );
    }

    @Override
    public Page<DepositPayInAccountDocument> newBasePayInRequests(Pageable pageable) {
        return documentPayInAccountDocumentRepository.findAll(DocumentPayInAccountSpecification.newBase(), pageable);
    }

    @Override
    public Page<DepositPayInAccountDocument> newAdditionalPayInRequests(Pageable pageable) {
        return documentPayInAccountDocumentRepository.findAll(
                    Specifications.where(
                            DocumentPayInAccountSpecification.newItems()
                    ).and(
                            DocumentPayInAccountSpecification.additional()
                    )
                , pageable);
    }

    @Override
    public Page<DepositOutcomeDocument> newEarlyOutcomeRequests(Pageable pageable) {
        return depositOutcomeDocumentRepository.findAll(
                Specifications.where(
                        DocumentOutcomeSpecification.stateNew()
                )
                        .and(DocumentOutcomeSpecification.early())
                , pageable
        );
    }

    @Override
    public DepositPayInAccountDocument getBasePayInDocument(Long depositId) {
        return (DepositPayInAccountDocument)
                documentPayInAccountDocumentRepository.findOne(
                        Specifications.where(
                                DocumentPayInAccountSpecification.newBase()
                        ).and(
                                DocumentPayInAccountSpecification.byDepositId(depositId)
                        )
                );
    }

    @Override
    public List<Document> getCapitalizationLog(Long depositId) {
        return depositPayMonthlyProfitDocumentRepository.findAll(
                Specifications.where(DocumentPayMonthlyProfitSpecification.byDepositId(depositId))
                        .and(DocumentPayMonthlyProfitSpecification.done())
                        .and(DocumentPayMonthlyProfitSpecification.orderByDate())
        );
    }

    @Override
    public List<Document> getPayInAccountLog(Long depositId) {
        return documentPayInAccountDocumentRepository.findAll(
                Specifications.where(DocumentPayInAccountSpecification.byDepositId(depositId))
                        .and(DocumentPayInAccountSpecification.done())
                        .and(DocumentPayInAccountSpecification.orderByDate())
        );
    }

    @Override
    public List<Document> getPayInAccountRequestsUndoneLog(Long depositId) {
        return documentPayInAccountDocumentRepository.findAll(
                Specifications.where(DocumentPayInAccountSpecification.byDepositId(depositId))
                        .and(DocumentPayInAccountSpecification.newItems())
                        .and(DocumentPayInAccountSpecification.orderByDate())
        );
    }

    @Override
    public void cancelRequestBeforeMoneyTransferByDocNumber(String code) throws DocumentNotFoundException, DocumentAlreadyProcessingException, RedSaldoException, IllegalDocumentStateException {
        Document document = billingService.getDocumentByNum(code);

        if (document instanceof DepositPayInAccountDocument) {
            if(!((DepositPayInAccountDocument) document).getState().equals(DepositPayInAccountDocument.State.NEW)){
                throw new IllegalDocumentStateException("document is not in NEW status already!");
            }

            if (!document.getAccountTransactions().isEmpty()) {
                throw new DocumentAlreadyProcessingException("Money transfer already started!");
            }

            ((DepositPayInAccountDocument) document).setState(DepositPayInAccountDocument.State.CANCELLED);
            documentPayInAccountDocumentRepository.save((DepositPayInAccountDocument) document);
        }

        if (document instanceof DepositOutcomeDocument) {
            billingService.cancelDepositOutcome((DepositOutcomeDocument) document);
        }


    }

    @Override
    public List<Document> getOutcomeLog(Long depositId) {
        return depositOutcomeDocumentRepository.findAll(
                Specifications.where(
                        DocumentOutcomeSpecification.byDepositId(depositId)
                )
                        .and(DocumentOutcomeSpecification.orderByDateDesc())
                        .and(DocumentOutcomeSpecification.done())
        );
    }

    @Override
    public List<Document> getOutcomeUndoneLog(Long depositId) {

        return depositOutcomeDocumentRepository.findAll(
                Specifications.where(
                        DocumentOutcomeSpecification.byDepositId(depositId)
                )
                        .and(DocumentOutcomeSpecification.orderByDateDesc())
                        .and(DocumentOutcomeSpecification.stateNew())
        );
    }


}
