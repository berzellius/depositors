package com.mfodepositorsacc.dto;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by berz on 27.04.15.
 */
public class SumSettingsDTO {

    public SumSettingsDTO(BigDecimal sumFrom, Integer minLength, Integer maxLength){
        this.sumFrom = sumFrom;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    public BigDecimal sumFrom;


    public Integer minLength;


    public Integer maxLength;

    public List<PercentsDTO> percentsList = new LinkedList<PercentsDTO>();
}
