package com.mfodepositorsacc.service;

import com.mfodepositorsacc.accountingmachine.dmodel.Document;
import com.mfodepositorsacc.accountingmachine.exception.RedSaldoException;
import com.mfodepositorsacc.billing.dmodel.DepositOutcomeDocument;
import com.mfodepositorsacc.billing.dmodel.DepositPayInAccountDocument;
import com.mfodepositorsacc.billing.exception.DocumentAlreadyProcessingException;
import com.mfodepositorsacc.billing.exception.DocumentNotFoundException;
import com.mfodepositorsacc.billing.exception.IllegalDocumentStateException;
import com.mfodepositorsacc.dmodel.Deposit;
import com.mfodepositorsacc.dto.DepositCalculation;
import com.mfodepositorsacc.exceptions.DepositCapitalizationIllegalStateException;
import com.mfodepositorsacc.repository.DepositRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by berz on 21.06.15.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DepositServiceImpl implements DepositService {
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    BillingSystemUtils billingSystemUtils;

    @Autowired
    DepositCalculationService depositCalculationService;

    @Autowired
    DepositRepository depositRepository;

    @Override
    public void capitalize(Deposit deposit) throws DepositCapitalizationIllegalStateException{

        System.out.println("capitalization of #".concat(deposit.getId().toString()).concat(" next capitalization ").concat(deposit.getNextCapitalization().toString()));

        BigDecimal saldo = billingSystemUtils.getDepositSaldo(deposit.getId());

        if(saldo.compareTo(BigDecimal.ZERO) < 1){
            System.out.println("Нет денег - нет прибыли");
            throw new DepositCapitalizationIllegalStateException("На счету нет денег. Капитализация невозможна");
        }

        if(deposit.getPercent() == null || deposit.getPercent().compareTo(BigDecimal.ZERO) < 1){
            throw new DepositCapitalizationIllegalStateException("Не задана процентная ставка. Каитализация невозможна");
        }

        DepositCalculation depositCalculationMonth = new DepositCalculation(saldo, 1, deposit.getDepositorFormType());
        depositCalculationMonth.percents = deposit.getPercent();

        BigDecimal sumToAdd = depositCalculationService.calculateSumInTheEnd(depositCalculationMonth).add(saldo.negate());

        billingSystemUtils.doDepositPayInMonthlyProfit(deposit.getId(), sumToAdd, deposit.getNextCapitalization());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(deposit.getNextCapitalization());
        calendar.add(Calendar.MONTH, 1);

        deposit.setNextCapitalization(calendar.getTime());

        entityManager.merge(deposit);
    }

    @Override
    public void dropCapitalizationDate(Deposit deposit) {
        deposit.setNextCapitalization(null);

        entityManager.merge(deposit);
    }

    @Override
    public HashMap<String, List<Document>> getMoneyMotionLogs(Deposit deposit) {
        HashMap<String, List<Document>> logs = new LinkedHashMap<String, List<Document>>();
        logs.put("capitalization", billingSystemUtils.getCapitalizationLog(deposit));
        logs.put("payinaccount", billingSystemUtils.getPayInAccountLog(deposit));
        logs.put("outcome", billingSystemUtils.getOutcomeLog(deposit));

        return logs;
    }

    @Override
    public DepositPayInAccountDocument requestAdditionalMoneyPayIn(Deposit deposit, BigDecimal sum){
        DepositPayInAccountDocument depositPayInAccountDocument = billingSystemUtils.doDepositAdditionalPayInAccountRequest(deposit.getId(), sum);

        return depositPayInAccountDocument;
    }

    @Override
    public HashMap<String, List<Document>> getMoneyMotionRequestsLogs(Deposit deposit) {
        HashMap<String, List<Document>> logs = new LinkedHashMap<String, List<Document>>();
        logs.put("payinaccount", billingSystemUtils.getPayInAccountRequestsUndoneLog(deposit));
        logs.put("outcome", billingSystemUtils.getOutcomeUndoneLog(deposit));

        return logs;
    }

    @Override
    public void cancelRequestToPayInByDocNumber(String code) throws DocumentNotFoundException, DocumentAlreadyProcessingException, RedSaldoException, IllegalDocumentStateException {
        billingSystemUtils.cancelRequestBeforeMoneyTransferByDocNumber(code);
    }

    @Override
    public DepositOutcomeDocument requestEarlyOutcome(Deposit deposit, BigDecimal sum) throws RedSaldoException {
        DepositOutcomeDocument depositOutcomeDocument = billingSystemUtils.doDepositEarlyOutcomeRequest(deposit.getId(), sum);
        return depositOutcomeDocument;
    }

    @Override
    public void cancelRequestToOutcomeByDocNumber(String code) throws DocumentAlreadyProcessingException, DocumentNotFoundException, RedSaldoException, IllegalDocumentStateException {
        billingSystemUtils.cancelRequestBeforeMoneyTransferByDocNumber(code);
    }

    @Override
    public void updatePercent(Deposit deposit, BigDecimal percent) {
        deposit.setPercent(percent);
        depositRepository.save(deposit);
    }


}
