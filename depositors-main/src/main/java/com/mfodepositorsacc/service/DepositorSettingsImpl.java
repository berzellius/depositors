package com.mfodepositorsacc.service;

import com.mfodepositorsacc.accountingmachine.utils.Assert;
import com.mfodepositorsacc.dmodel.DepositorTypeSettings;
import com.mfodepositorsacc.dmodel.Percents;
import com.mfodepositorsacc.dmodel.SumSettings;
import com.mfodepositorsacc.exceptions.DepositSettingsSavingException;
import com.mfodepositorsacc.repository.DepositorTypeSettingsRepository;
import com.mfodepositorsacc.repository.PercentsRepository;
import com.mfodepositorsacc.repository.SumSettingsRepository;
import com.mfodepositorsacc.specifications.SumSettingsSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by berz on 26.05.15.
 */
@Service
@Transactional
public class DepositorSettingsImpl implements DepositorSettings {

    @Autowired
    PercentsRepository percentsRepository;

    @Autowired
    SumSettingsRepository sumSettingsRepository;

    @Autowired
    DepositorTypeSettingsRepository depositorTypeSettingsRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public void updatePercents(Percents percents, Percents percentsModified) throws DepositSettingsSavingException {

        percentsModified.setId(percents.getId());
        checkValues(percentsModified, percents.getSumSettings());

        if (
                percentsModified.getLengthFrom() >= percents.getSumSettings().getMaxLength() ||
                        percentsModified.getLengthFrom() < percents.getSumSettings().getMinLength()
                ) {
            throw new DepositSettingsSavingException("length out of range!", DepositSettingsSavingException.Reason.LENGTH_OUT_OF_RANGE);
        }

        percents.setPercent(percentsModified.getPercent());
        percents.setLengthFrom(percentsModified.getLengthFrom());

        percentsRepository.save(percents);
    }

    @Override
    public void addPercents(Percents percents) throws DepositSettingsSavingException {

        Assert.isNotNull(percents.getSumSettings());

        checkValues(percents, percents.getSumSettings());

        if (
                percents.getLengthFrom() >= percents.getSumSettings().getMaxLength() ||
                        percents.getLengthFrom() < percents.getSumSettings().getMinLength()
                ) {
            throw new DepositSettingsSavingException("length out of range!", DepositSettingsSavingException.Reason.LENGTH_OUT_OF_RANGE);
        }

        percentsRepository.save(percents);
        entityManager.refresh(percents.getSumSettings());
    }

    private void checkValues(Percents percents, SumSettings sumSettings) throws DepositSettingsSavingException {
        if (
                percents.getLengthFrom() == null || percents.getPercent() == null
                ) {
            throw new DepositSettingsSavingException("null values not allowed", DepositSettingsSavingException.Reason.EMPTY_FIELDS);
        }

        if (percents.getLengthFrom() == 0 || percents.getPercent().equals(BigDecimal.ZERO)) {
            throw new DepositSettingsSavingException("zero values not allowed", DepositSettingsSavingException.Reason.ZERO_VALUES);
        }

        for (Percents p : sumSettings.getPercentsList()) {
            if (
                    p.getLengthFrom().equals(percents.getLengthFrom()) &&
                            (percents.getId() == null || !percents.getId().equals(p.getId()))
                    ) {
                throw new DepositSettingsSavingException("duplicate lengthFrom", DepositSettingsSavingException.Reason.DUPLICATE);
            }
        }
    }

    @Override
    public void delPercents(Percents percents) {
        percentsRepository.delete(percents);
    }

    @Override
    public List<Long> nonConsistentSumSettings() {
        List<Long> idList = new LinkedList<Long>();

        Iterable<SumSettings> sumSettingsIterable = sumSettingsRepository.findAll(SumSettingsSpecifications.nonConsistent());

        for (SumSettings sumSettings : sumSettingsIterable) {
            idList.add(sumSettings.getId());
        }

        return idList;
    }

    @Override
    public void updateSumSettings(SumSettings sumSettings, SumSettings sumSettingsModified) throws DepositSettingsSavingException {
        sumSettingsModified.setId(sumSettings.getId());
        checkValuesSumSettings(sumSettingsModified, sumSettings.getDepositorTypeSettings());

        sumSettings.setSumFrom(sumSettingsModified.getSumFrom());

        if (sumSettingsModified.getMinLength() < sumSettingsModified.getMaxLength()) {
            sumSettings.setMinLength(sumSettingsModified.getMinLength());
            sumSettings.setMaxLength(sumSettingsModified.getMaxLength());
        }
        else{
            sumSettings.setMinLength(sumSettingsModified.getMaxLength());
            sumSettings.setMaxLength(sumSettingsModified.getMinLength());
        }


        for (Percents p : sumSettings.getPercentsList()) {
            if (p.getLengthFrom() < sumSettings.getMinLength() || p.getLengthFrom() > sumSettings.getMaxLength()) {
                percentsRepository.delete(p);
            }
        }
        sumSettingsRepository.save(sumSettings);
        entityManager.flush();
        entityManager.refresh(sumSettings);

    }

    @Override
    public void delSumSettings(SumSettings sumSettings) {
        for (Percents p : sumSettings.getPercentsList()) {
            percentsRepository.delete(p);
        }

        sumSettingsRepository.delete(sumSettings);
    }

    @Override
    public void addSumSettings(SumSettings sumSettings) throws DepositSettingsSavingException {

        Assert.isNotNull(sumSettings.getDepositorTypeSettings());
        checkValuesSumSettings(sumSettings, sumSettings.getDepositorTypeSettings());

        if(sumSettings.getMinLength() >= sumSettings.getMaxLength()){
            Integer s = sumSettings.getMinLength();
            sumSettings.setMinLength(sumSettings.getMaxLength());
            sumSettings.setMaxLength(s);
        }

        sumSettingsRepository.save(sumSettings);
        entityManager.refresh(sumSettings.getDepositorTypeSettings());
    }

    @Override
    public boolean checkConsistent(DepositorTypeSettings depositorTypeSettings) {

        for(SumSettings sumSettings : depositorTypeSettings.getSumSettingsList()){
            if(sumSettings.getSumFrom().equals(depositorTypeSettings.getMinSum()))
                return true;
        }

        return false;
    }

    @Override
    public List<Long> nonConsistentDepositorTypeSettings() {
        List<Long> idList = new LinkedList<Long>();

        for(DepositorTypeSettings depositorTypeSettings : depositorTypeSettingsRepository.findAll()){
            if(!checkConsistent(depositorTypeSettings))
                idList.add(depositorTypeSettings.getId());
        }

        return idList;
    }

    private void checkValuesSumSettings(SumSettings sumSettingsModified, DepositorTypeSettings depositorTypeSettings) throws DepositSettingsSavingException {
        if (
                sumSettingsModified.getSumFrom() == null ||
                        sumSettingsModified.getMinLength() == null ||
                        sumSettingsModified.getMaxLength() == null
                )
            throw new DepositSettingsSavingException("empty fields", DepositSettingsSavingException.Reason.EMPTY_FIELDS);

        // Сумма "от" меньше минимально допустимой
        if (sumSettingsModified.getSumFrom().compareTo(depositorTypeSettings.getMinSum()) == -1)
            throw new DepositSettingsSavingException("sumFrom less than minimum sum", DepositSettingsSavingException.Reason.SUM_OUT_OF_RANGE);

        // Сумма "от" больше или равна максимально допустимой
        if (sumSettingsModified.getSumFrom().compareTo(depositorTypeSettings.getMaxSum()) > -1) {
            throw new DepositSettingsSavingException("sumFrom more or equal maximum sum", DepositSettingsSavingException.Reason.SUM_OUT_OF_RANGE);
        }

        // Сумма "от" - поворяющееся значение
        for (SumSettings sumSettings : depositorTypeSettings.getSumSettingsList()) {
            if (
                    sumSettings.getSumFrom().equals(sumSettingsModified.getSumFrom()) &&
                            (sumSettingsModified.getId() == null || !sumSettingsModified.getId().equals(sumSettings.getId()))
                    ) {
                throw new DepositSettingsSavingException("duplicate sumFrom", DepositSettingsSavingException.Reason.DUPLICATE);
            }
        }
    }
}
