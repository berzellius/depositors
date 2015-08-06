package com.mfodepositorsacc.billing.service;

import com.mfodepositorsacc.accountingmachine.dmodel.Document;
import com.mfodepositorsacc.accountingmachine.exception.RedSaldoException;
import com.mfodepositorsacc.billing.dmodel.DepositOutcomeDocument;
import com.mfodepositorsacc.billing.dmodel.DepositPayInAccountDocument;
import com.mfodepositorsacc.billing.exception.DocumentAlreadyProcessingException;
import com.mfodepositorsacc.billing.exception.DocumentNotFoundException;
import com.mfodepositorsacc.billing.exception.IllegalDocumentStateException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by berz on 18.01.15.
 */
public interface DocumentsService {
    List<DepositPayInAccountDocument> getClientPayInDocumentsActual(Long id);

    Page<DepositPayInAccountDocument> getClientsPayInDocumentsActual(Pageable pageable);

    Long countNewBasePayInRequests();

    Long countNewAdditionalPayInRequests();

    Long countNewEarlyOutcomeRequests();

    Page<DepositPayInAccountDocument> newBasePayInRequests(Pageable pageable);

    Page<DepositPayInAccountDocument> newAdditionalPayInRequests(Pageable pageable);

    Page<DepositOutcomeDocument> newEarlyOutcomeRequests(Pageable pageable);

    DepositPayInAccountDocument getBasePayInDocument(Long depositId);


    /**
     * Лог капитализаций вклада
     * @param depositId - номер счета
     * @return
     */
    List<Document> getCapitalizationLog(Long depositId);

    List getPayInAccountLog(Long depositId);

    List<Document> getPayInAccountRequestsUndoneLog(Long depositId);

    void cancelRequestBeforeMoneyTransferByDocNumber(String code) throws DocumentNotFoundException, DocumentAlreadyProcessingException, RedSaldoException, IllegalDocumentStateException;

    List getOutcomeLog(Long depositId);

    List<Document> getOutcomeUndoneLog(Long depositId);
}
