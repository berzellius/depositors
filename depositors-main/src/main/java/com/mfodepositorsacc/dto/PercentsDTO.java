package com.mfodepositorsacc.dto;

import com.mfodepositorsacc.dmodel.Percents;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by berz on 27.04.15.
 */
public class PercentsDTO {

    public PercentsDTO(Integer lengthFrom, BigDecimal percent){
        this.lengthFrom = lengthFrom;
        this.percent = percent;
    }

    public Integer lengthFrom;

    public BigDecimal percent;

    public static List<PercentsDTO> getDTO(Iterable<Percents> percentsList) {
        List<PercentsDTO> percentsDTOList = new LinkedList<PercentsDTO>();
        for(Percents percents : percentsList){
            percentsDTOList.add(new PercentsDTO(percents.getLengthFrom(), percents.getPercent()));
        }

        return percentsDTOList;
    }
}
