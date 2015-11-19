package com.mfodepositorsacc.service;

import com.mfodepositorsacc.accountingmachine.dmodel.Document;
import com.mfodepositorsacc.accountingmachine.exception.RedSaldoException;
import com.mfodepositorsacc.billing.dmodel.DepositOutcomeDocument;
import com.mfodepositorsacc.billing.dmodel.DepositPayInAccountDocument;
import com.mfodepositorsacc.billing.exception.DocumentAlreadyProcessingException;
import com.mfodepositorsacc.billing.exception.DocumentNotFoundException;
import com.mfodepositorsacc.billing.exception.IllegalDocumentStateException;
import com.mfodepositorsacc.dmodel.Deposit;
import com.mfodepositorsacc.dmodel.User;
import com.mfodepositorsacc.dto.DepositCalculation;
import com.mfodepositorsacc.exceptions.DepositCapitalizationIllegalStateException;
import com.mfodepositorsacc.repository.DepositRepository;
import com.mfodepositorsacc.util.fwMoney;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

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
    public HashMap<String, Object> mainContractPdfPlaceholders(Deposit deposit){
        HashMap<String, Object> pl = new LinkedHashMap<String, Object>();
        pl.put("$CONTRACT_NUMBER", deposit.getId());
        pl.put("$CITY", "Москва");
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("ru"));
        pl.put("$CONTRACT_DATE", sdf.format(deposit.getDtmCreate()));
        pl.put("$FIO", deposit.getDepositor().getFio());
        pl.put("$ADDR", deposit.getDepositor().getRegIndex().concat(" ").concat(deposit.getDepositor().getRegCity().toString()));
        pl.put("$PASSPORT", deposit.getDepositor().getPassportNum().concat(", выдан ").concat(deposit.getDepositor().getDepartmentName()).concat(" (").concat(deposit.getDepositor().getDepartmentCode()).concat(") ").concat(sdf.format(deposit.getDepositor().getPassportAccepted())));
        pl.put("$SUM_DIG", deposit.getSum().intValue());
        fwMoney fwMoney = new fwMoney(deposit.getSum().doubleValue());
        pl.put("$SUM_WORDS", fwMoney.num2str(true, false).trim());
        fwMoney fwMoneyPercents = new fwMoney(deposit.getPercent().doubleValue());
        pl.put("$YEAR_PERCENTS_WORDS",
                fwMoneyPercents.num2str(false, true, com.mfodepositorsacc.util.fwMoney.Type.NUMBER).trim()
        );
        pl.put("$YEAR_PERCENTS", deposit.getPercent().doubleValue());
        fwMoney fwMoneyLength = new fwMoney(deposit.getLength());
        pl.put("$LENGTH_MONTH_WORDS",  fwMoneyLength.num2str(true, false).trim());
        pl.put("$LENGTH_MONTH", deposit.getLength());

        DepositCalculation depositCalculation = new DepositCalculation();
        depositCalculation.setLength(deposit.getLength());
        depositCalculation.setSum(deposit.getSum());
        depositCalculation.percents = deposit.getPercent();
        BigDecimal sumInTheEnd = depositCalculationService.calculateSumInTheEnd(depositCalculation);
        BigDecimal profit = sumInTheEnd.add(deposit.getSum().negate());

        BigDecimal profitInPercents = profit.multiply(BigDecimal.valueOf(100)).divide(deposit.getSum(), 2, RoundingMode.HALF_UP);
        fwMoney fwMoneyProfitPercents = new fwMoney(profitInPercents.doubleValue());
        pl.put("$PROFIT_PERCENTS_WORDS", fwMoneyProfitPercents.num2str(false, true, com.mfodepositorsacc.util.fwMoney.Type.NUMBER).trim());
        pl.put("$PROFIT_PERCENTS", profitInPercents.doubleValue());

        BigDecimal tax = profit.multiply(BigDecimal.valueOf(0.13));
        BigDecimal cleanProfit = profit.add(tax.negate());
        BigDecimal monthlyProfit = cleanProfit.divide(BigDecimal.valueOf(deposit.getLength()), 2, RoundingMode.HALF_UP);
        fwMoney fwMoneyTax = new fwMoney(tax.doubleValue());
        pl.put("$TAX_ALL", fwMoneyTax.num2str(false, false, com.mfodepositorsacc.util.fwMoney.Type.NUMBER_AND_MONEY));
        fwMoney fwMoneyProfit = new fwMoney(cleanProfit.doubleValue());
        pl.put("$CLEAN_PROFIT_ALL", fwMoneyProfit.num2str(false, false, com.mfodepositorsacc.util.fwMoney.Type.NUMBER_AND_MONEY));
        fwMoney fwMoneyMonthlyProfit = new fwMoney(monthlyProfit.doubleValue());
        pl.put("$MONTHLY_PROFIT", fwMoneyMonthlyProfit.num2str(false, false, com.mfodepositorsacc.util.fwMoney.Type.NUMBER_AND_MONEY));

        return pl;
    }

    private String getPercentsTailHelper(BigDecimal percents){
        return ((percents.doubleValue() - Math.floor(percents.doubleValue())) > 0) ?
                " целых " + Math.round((percents.doubleValue() - Math.floor(percents.doubleValue()))*100) + " сотых" : "";
    }

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
