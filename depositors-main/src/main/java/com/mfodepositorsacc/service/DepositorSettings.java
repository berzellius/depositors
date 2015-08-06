package com.mfodepositorsacc.service;

import com.mfodepositorsacc.dmodel.DepositorTypeSettings;
import com.mfodepositorsacc.dmodel.Percents;
import com.mfodepositorsacc.dmodel.SumSettings;
import com.mfodepositorsacc.exceptions.DepositSettingsSavingException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by berz on 26.05.15.
 */
@Service
public interface DepositorSettings {


    void updatePercents(Percents percents, Percents percentsModified) throws DepositSettingsSavingException;

    void addPercents(Percents percents) throws DepositSettingsSavingException;

    void delPercents(Percents percents);


    List<Long> nonConsistentSumSettings();

    void updateSumSettings(SumSettings sumSettings, SumSettings sumSettingsModified) throws DepositSettingsSavingException;

    void delSumSettings(SumSettings sumSettings);

    void addSumSettings(SumSettings sumSettings) throws DepositSettingsSavingException;

    boolean checkConsistent(DepositorTypeSettings depositorTypeSettings);

    List<Long> nonConsistentDepositorTypeSettings();

}
