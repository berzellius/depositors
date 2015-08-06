package com.mfodepositorsacc.service;

import com.mfodepositorsacc.accountingmachine.dmodel.Document;
import com.mfodepositorsacc.accountingmachine.utils.Assert;
import com.mfodepositorsacc.billing.dmodel.DepositPayInAccountDocument;
import com.mfodepositorsacc.billing.exception.DuplicatePaymentException;
import com.mfodepositorsacc.dmodel.*;
import com.mfodepositorsacc.dto.DepositCalculation;
import com.mfodepositorsacc.dto.DepositMarketingDTO;
import com.mfodepositorsacc.exceptions.WrongInputDataException;
import com.mfodepositorsacc.repository.*;
import com.mfodepositorsacc.specifications.PercentsSpecifications;
import com.mfodepositorsacc.util.DateUtil;
import com.mfodepositorsacc.util.UserLoginUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by berz on 29.04.15.
 */
@Service
@Transactional
public class DepositCalculationServiceImpl implements DepositCalculationService {

    @Autowired
    PercentsRepository percentsRepository;

    @Autowired
    DateUtil dateUtil;

    @Autowired
    DepositRepository depositRepository;

    @Autowired
    DepositorRepository depositorRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    UserLoginUtil userLoginUtil;

    @Autowired
    BillingSystemUtils billingSystemUtils;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public DepositCalculation calculateDeposit(DepositCalculation depositCalculation) {
        Assert.isTrue(depositCalculation.sum != null);
        Assert.isTrue(depositCalculation.length != null);
        Assert.isTrue(depositCalculation.depositorFormType != null);

        Iterable<Percents> percentsList = percentsRepository.findAll(
                PercentsSpecifications.forDepositCalculation(depositCalculation)
        );

        if (percentsList.iterator().hasNext()) {
            depositCalculation.percents = percentsList.iterator().next().getPercent();

            depositCalculation.sumInTheEnd = calculateSumInTheEnd(depositCalculation);

            depositCalculation.profit = depositCalculation.sumInTheEnd.add(depositCalculation.sum.negate());
        }


        return depositCalculation;
    }

    @Override
    public BigDecimal calculateSumInTheEnd(DepositCalculation depositCalculation) {
        Assert.isNotNull(depositCalculation.sum);
        Assert.isNotNull(depositCalculation.length);
        Assert.isNotNull(depositCalculation.percents);

        BigDecimal sumInTheEnd = depositCalculation.sum;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        Calendar calendar1 = (Calendar) calendar.clone();
        calendar1.set(Calendar.MONTH, calendar1.get(Calendar.MONTH) + 1);


        for (int m = 1; m <= depositCalculation.length; m++) {

            BigDecimal add = sumInTheEnd.multiply(
                    calculateAddToSumAsMultiplier(calendar, calendar1, depositCalculation.percents)
            );

            sumInTheEnd = sumInTheEnd.add(
                   add.setScale(2, RoundingMode.HALF_UP)
            );

            //System.out.println(" + " + add.setScale(0, RoundingMode.HALF_UP) + " = " + sumInTheEnd.setScale(0, RoundingMode.HALF_UP));

            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
            calendar1.set(Calendar.MONTH, calendar1.get(Calendar.MONTH) + 1);
        }

        return sumInTheEnd.setScale(0, RoundingMode.HALF_UP);

    }

    @Override
    public BigDecimal calculateAddToSumAsMultiplier(Calendar calendar, Calendar calendar1, BigDecimal percents) {

        int days = dateUtil.getDaysBetweenDays(calendar, calendar1);
        int daysInYear = calendar.getActualMaximum(Calendar.DAY_OF_YEAR);

        return percents.multiply(BigDecimal.valueOf(days)).divide(
                BigDecimal.valueOf(100).multiply(BigDecimal.valueOf(daysInYear)),
                16, RoundingMode.HALF_UP
        );
    }

    @Override
    public BigDecimal calculateSumAddInDay(BigDecimal sum, BigDecimal percent, Calendar calendar) {
        int daysInYear = calendar.getActualMaximum(Calendar.DAY_OF_YEAR);

        return sum.multiply(
                percent.multiply(
                        BigDecimal.valueOf(1)).divide(
                        BigDecimal.valueOf(100).multiply(BigDecimal.valueOf(daysInYear)),
                        16, RoundingMode.HALF_UP
                )
        ).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public void processRegistration(Deposit deposit, Depositor depositor) throws WrongInputDataException {

        validateSignupData(deposit, depositor);

        depositor.setDtmCreate(new Date());


        DepositCalculation depositCalculation = calculateDeposit(new DepositCalculation(deposit.getSum(), deposit.getLength(), deposit.getDepositorFormType()));

        if(depositCalculation.percents == null || depositCalculation.sumInTheEnd == null){
            throw new WrongInputDataException("Rules for this sum/length/depositorFormType does not exests!", WrongInputDataException.Reason.DEPOSIT_CALCULATION);
        }

        depositorRepository.save(depositor);

        deposit.setPercent(depositCalculation.percents);
        deposit.setDtmCreate(new Date());
        deposit.setActivated(false);
        deposit.setDepositor(depositor);

        depositRepository.save(deposit);

        User user = new User();
        user.setDeposit(deposit);
        user.setUsername(deposit.getId().toString());
        user.setEnabled(false);
        user.setActivationCode(getRandomStringForActivationCode());

        userLoginUtil.addUserWithRole(user, UserRole.Role.DEPOSITOR);

        EmailNotification activateNotification = new EmailNotification();
        activateNotification.setStatus(Notification.Status.WAITING);
        activateNotification.setTemplate(Notification.Template.PROFILE_ACTIVATION);
        activateNotification.setUser(user);

        notificationRepository.save(activateNotification);

        try {
            billingSystemUtils.doDepositBasePayInAccountRequest(deposit.getId(), depositCalculation.sum);
        } catch (DuplicatePaymentException e) {
            // Депозит только что создан
        }
    }


    @Override
    public void validateSignupData(Deposit deposit, Depositor depositor) {
        // TODO
    }

    @Override
    public DepositMarketingDTO calculteDepositMarketingData(Deposit deposit, BigDecimal currentSum) {
        DepositMarketingDTO depositMarketingDTO = new DepositMarketingDTO();

        DepositCalculation depositCalculationYear = new DepositCalculation(currentSum, 12, deposit.getDepositorFormType());
        depositCalculationYear.percents = deposit.getPercent();
        DepositCalculation depositCalculationMonth = new DepositCalculation(currentSum, 1, deposit.getDepositorFormType());
        depositCalculationMonth.percents = deposit.getPercent();

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());

        BigDecimal inYear = calculateSumInTheEnd(depositCalculationYear).add(currentSum.negate());
        BigDecimal inMonth = calculateSumInTheEnd(depositCalculationMonth).add(currentSum.negate());
        BigDecimal inDay = calculateSumAddInDay(currentSum, deposit.getPercent(), c);
        BigDecimal inWeek = inDay.multiply(BigDecimal.valueOf(7));

        depositMarketingDTO.setInYear(inYear);
        depositMarketingDTO.setInMonth(inMonth);
        depositMarketingDTO.setInDay(inDay);
        depositMarketingDTO.setInWeek(inWeek);

        depositMarketingDTO.setPercentsInYear(BigDecimal.valueOf(100).multiply(inYear).divide(currentSum, 2, RoundingMode.HALF_UP));
        depositMarketingDTO.setPercentsInMonth(BigDecimal.valueOf(100).multiply(inMonth).divide(currentSum, 2, RoundingMode.HALF_UP));
        depositMarketingDTO.setPercentsInWeek(BigDecimal.valueOf(100).multiply(inWeek).divide(currentSum, 2, RoundingMode.HALF_UP));
        depositMarketingDTO.setPercentsInDay(BigDecimal.valueOf(100).multiply(inDay).divide(currentSum, 2, RoundingMode.HALF_UP));

        return depositMarketingDTO;
    }


    @Override
    public String getRandomStringForActivationCode() {
        return UUID.randomUUID().toString();
    }

    @Override
    public Document getPayInBillMustBeCleared(Deposit deposit) {
        DepositPayInAccountDocument document = billingSystemUtils.getBasePayInAccountDocumentByDepositId(deposit.getId());
        return (document != null && document.getState().equals(DepositPayInAccountDocument.State.NEW))? document : null;
    }

}
