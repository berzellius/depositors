package com.mfodepositorsacc.web;

import com.mfodepositorsacc.repository.SumSettingsRepository;
import com.mfodepositorsacc.service.DepositorSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by berz on 21.05.15.
 */
@RequestMapping(value = "/administrator")
@RestController
public class AdminRestController {

    @Autowired
    SumSettingsRepository sumSettingsRepository;

    @Autowired
    DepositorSettings depositorSettings;

    @RequestMapping(value = "/nonconsistent")
    public Iterable<Long> nonConsistent(){
        return depositorSettings.nonConsistentSumSettings();
    }

}
