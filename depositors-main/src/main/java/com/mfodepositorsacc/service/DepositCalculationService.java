package com.mfodepositorsacc.service;

import com.mfodepositorsacc.accountingmachine.dmodel.Document;
import com.mfodepositorsacc.dmodel.Deposit;
import com.mfodepositorsacc.dmodel.Depositor;
import com.mfodepositorsacc.dto.DepositCalculation;
import com.mfodepositorsacc.dto.DepositMarketingDTO;
import com.mfodepositorsacc.exceptions.WrongInputDataException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * Created by berz on 29.04.15.
 */
@Service
public interface DepositCalculationService {

    public DepositCalculation calculateDeposit(DepositCalculation depositCalculation);

    BigDecimal calculateSumInTheEnd(DepositCalculation depositCalculation);

    BigDecimal calculateAddToSumAsMultiplier(Calendar calendar, Calendar calendar1, BigDecimal percents);

    BigDecimal calculateSumAddInDay(BigDecimal sum, BigDecimal percent, Calendar calendar);

    void processRegistration(Deposit deposit, Depositor depositor) throws WrongInputDataException;

    void validateSignupData(Deposit deposit, Depositor depositor);

    DepositMarketingDTO calculteDepositMarketingData(Deposit deposit, BigDecimal currentSum);


    String getRandomStringForActivationCode();

    Document getPayInBillMustBeCleared(Deposit deposit);
}
