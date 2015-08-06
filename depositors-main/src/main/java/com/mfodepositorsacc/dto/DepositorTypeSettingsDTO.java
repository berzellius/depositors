package com.mfodepositorsacc.dto;

import com.mfodepositorsacc.dmodel.DepositorTypeSettings;
import com.mfodepositorsacc.dmodel.Percents;
import com.mfodepositorsacc.dmodel.SumSettings;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by berz on 27.04.15.
 */
public class DepositorTypeSettingsDTO {

    public DepositorTypeSettingsDTO(DepositorTypeSettings depositorTypeSettings){
        this.minSum = depositorTypeSettings.getMinSum();
        this.maxSum = depositorTypeSettings.getMaxSum();
        this.depositorFormType = depositorTypeSettings.getDepositorFormType();
        SumSettingsDTO sumSettingsDTOCurrent;
        for(SumSettings sumSettings : depositorTypeSettings.getSumSettingsList()){
            sumSettingsDTOCurrent = new SumSettingsDTO(sumSettings.getSumFrom(), sumSettings.getMinLength(), sumSettings.getMaxLength());
            for(Percents percents : sumSettings.getPercentsList()){
                sumSettingsDTOCurrent.percentsList.add(new PercentsDTO(percents.getLengthFrom(), percents.getPercent()));
            }
            this.sumSettingsList.add(sumSettingsDTOCurrent);
        }
    }

    public BigDecimal minSum;


    public BigDecimal maxSum;


    public DepositorTypeSettings.DepositorFormType depositorFormType;


    public List<SumSettingsDTO> sumSettingsList = new LinkedList<SumSettingsDTO>();
}
