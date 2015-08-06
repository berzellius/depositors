package com.mfodepositorsacc.web;

import com.mfodepositorsacc.dmodel.City;
import com.mfodepositorsacc.dmodel.DepositorTypeSettings;
import com.mfodepositorsacc.dmodel.Region;
import com.mfodepositorsacc.dto.DepositCalculation;
import com.mfodepositorsacc.dto.DepositorTypeSettingsDTO;
import com.mfodepositorsacc.dto.PercentsDTO;
import com.mfodepositorsacc.repository.CityRepository;
import com.mfodepositorsacc.repository.DepositorTypeSettingsRepository;
import com.mfodepositorsacc.repository.PercentsRepository;
import com.mfodepositorsacc.repository.RegionRepository;
import com.mfodepositorsacc.service.DepositCalculationService;
import com.mfodepositorsacc.specifications.PercentsSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * Created by berz on 27.04.15.
 */
@RestController
@RequestMapping("/rest/signup")
public class SignUpRestController {

    @Autowired
    DepositorTypeSettingsRepository depositorTypeSettingsRepository;

    @Autowired
    PercentsRepository percentsRepository;

    @Autowired
    DepositCalculationService depositCalculationService;

    @Autowired
    RegionRepository regionRepository;

    @Autowired
    CityRepository cityRepository;

    @RequestMapping
    DepositorTypeSettingsDTO getDepositorTypeSettings(
            @RequestParam(value = "type")
            DepositorTypeSettings.DepositorFormType depositorFormType
    ){
        DepositorTypeSettings depositorTypeSettings = depositorTypeSettingsRepository.findOneByDepositorFormType(depositorFormType);

        DepositorTypeSettingsDTO depositorTypeSettingsDTO = new DepositorTypeSettingsDTO(depositorTypeSettings);

        return depositorTypeSettingsDTO;
    }

    @RequestMapping(value = "percents")
    Iterable<PercentsDTO> getPercents(
            @RequestParam(value = "sum")
            BigDecimal sum,
            @RequestParam(value = "length")
            Integer length,
            @RequestParam(value = "type")
            DepositorTypeSettings.DepositorFormType depositorFormType
    ){
        return PercentsDTO.getDTO(
                    percentsRepository.findAll(PercentsSpecifications.forDepositCalculation(new DepositCalculation(sum, length, depositorFormType))
                ));
    }

    @RequestMapping(value = "deposit_calculation")
    DepositCalculation depositCalculationWeb(
            @RequestParam(value = "sum")
            BigDecimal sum,
            @RequestParam(value = "length")
            Integer length,
            @RequestParam(value = "type")
            DepositorTypeSettings.DepositorFormType depositorFormType
    ){

        DepositCalculation depositCalculation = new DepositCalculation(sum, length, depositorFormType);

        depositCalculation = depositCalculationService.calculateDeposit(depositCalculation);

        return depositCalculation;
    }

    @RequestMapping(value = "region_list")
    Iterable<Region> regions(){
        return regionRepository.findAll();
    }

    @RequestMapping(value = "cities_by_region")
    Iterable<City> cities(
            @RequestParam(value = "region_id")
            Region region
    ){
        return cityRepository.findByRegion(region);
    }


}
