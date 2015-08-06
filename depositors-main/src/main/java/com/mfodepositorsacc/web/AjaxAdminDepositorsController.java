package com.mfodepositorsacc.web;

import com.mfodepositorsacc.dmodel.*;
import com.mfodepositorsacc.exceptions.DepositSettingsSavingException;
import com.mfodepositorsacc.repository.DepositorTypeSettingsRepository;
import com.mfodepositorsacc.repository.PercentsRepository;
import com.mfodepositorsacc.repository.SumSettingsRepository;
import com.mfodepositorsacc.service.DepositorSettings;
import com.mfodepositorsacc.util.UserLoginUtil;
import javassist.tools.web.BadHttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Created by berz on 19.05.15.
 */
@Controller
@RequestMapping(value = "/ajax/admin/depositors")
public class AjaxAdminDepositorsController {

    @Autowired
    UserLoginUtil userLoginUtil;

    @Autowired
    DepositorSettings depositorSettings;

    @Autowired
    PercentsRepository percentsRepository;

    @Autowired
    SumSettingsRepository sumSettingsRepository;

    @Autowired
    DepositorTypeSettingsRepository depositorTypeSettingsRepository;

    @RequestMapping(value = "info")
    public String info(
            Model model,
            @RequestParam(value = "user_id")
            User user
    ) throws BadHttpRequest {

        if (user == null || !userLoginUtil.userHaveRole(user, UserRole.Role.DEPOSITOR)) {
            throw new BadHttpRequest();
        }

        model.addAttribute("user", user);

        return "ajax/admin/depositors/info";
    }


    @RequestMapping(value = "/sumsettings/{id}", method = RequestMethod.PUT)
    public String sumSettings(
            @ModelAttribute("id")
            SumSettings sumSettings,
            @RequestBody
            SumSettings sumSettingsModified,
            Model model
    ){

        try {
            depositorSettings.updateSumSettings(sumSettings, sumSettingsModified);
        } catch (DepositSettingsSavingException e) {
            model.addAttribute("reason", e.getReason());
        }

        model.addAttribute("nonConsistentSumSettings", depositorSettings.nonConsistentSumSettings());
        DepositorTypeSettings depositorTypeSettings = depositorTypeSettingsRepository.findOne(sumSettings.getDepositorTypeSettings().getId());
        model.addAttribute("nonConsistent", !depositorSettings.checkConsistent(depositorTypeSettings));
        model.addAttribute("depositorTypeSettings", depositorTypeSettings);

        return "ajax/admin/depositors/sumsettings_save";
    }

    @RequestMapping(value = "/sumsettings", method = RequestMethod.POST)
    public String sumSettingsAdd(
            SumSettings sumSettings,
            Model model
    ){

        try {
            depositorSettings.addSumSettings(sumSettings);
        } catch (DepositSettingsSavingException e) {
            model.addAttribute("reason", e.getReason());
        }

        model.addAttribute("nonConsistentSumSettings", depositorSettings.nonConsistentSumSettings());

        DepositorTypeSettings depositorTypeSettings = depositorTypeSettingsRepository.findOne(sumSettings.getDepositorTypeSettings().getId());

        model.addAttribute("nonConsistent", !depositorSettings.checkConsistent(depositorTypeSettings));
        model.addAttribute("depositorTypeSettings", depositorTypeSettings);

        return "ajax/admin/depositors/sumsettings_save";
    }

    @RequestMapping(value = "/sumsettings/{id}", method = RequestMethod.DELETE)
    public String sumSettingsDel(
            @ModelAttribute("id")
            SumSettings sumSettings,
            Model model
    ){
        depositorSettings.delSumSettings(sumSettings);

        model.addAttribute("nonConsistentSumSettings", depositorSettings.nonConsistentSumSettings());
        DepositorTypeSettings depositorTypeSettings = depositorTypeSettingsRepository.findOne(sumSettings.getDepositorTypeSettings().getId());
        model.addAttribute("depositorTypeSettings", depositorTypeSettings);
        model.addAttribute("nonConsistent", !depositorSettings.checkConsistent(depositorTypeSettings));
        return "ajax/admin/depositors/sumsettings_save";
    }

    @RequestMapping(value = "/percents/{id}", method = RequestMethod.PUT)
    public String percents(
            @ModelAttribute("id")
            Percents percents,
            @RequestBody
            Percents percentsModified,
            Model model
    ) {

        try {
            depositorSettings.updatePercents(percents, percentsModified);
        } catch (DepositSettingsSavingException e) {
            model.addAttribute("reason", e.getReason());
        }

        Percents percents1 = percentsRepository.findOne(percents.getId());

        model.addAttribute("sumSettings", percents1.getSumSettings());

        if(depositorSettings.nonConsistentSumSettings().contains(percents1.getSumSettings().getId())){
            model.addAttribute("nonConsistent", true);
        }

        return "ajax/admin/depositors/percents_save";
    }

    @RequestMapping(value = "/percents", method = RequestMethod.POST)
    public String percentsAdd(
            Percents percents,
            Model model
    ) {

        try {
            depositorSettings.addPercents(percents);
        } catch (DepositSettingsSavingException e) {
            model.addAttribute("reason", e.getReason());
        }

        model.addAttribute("sumSettings", sumSettingsRepository.findOne(percents.getSumSettings().getId()));

        if(depositorSettings.nonConsistentSumSettings().contains(percents.getSumSettings().getId())){
            model.addAttribute("nonConsistent", true);
        }

        return "ajax/admin/depositors/percents_save";
    }

    @RequestMapping(value = "/percents/{id}", method = RequestMethod.DELETE)
    public String percentsDel(
            @ModelAttribute("id")
            Percents percents,
            Model model
    ) {
        Long sumSettingsId = percents.getSumSettings().getId();

        depositorSettings.delPercents(percents);

        model.addAttribute("sumSettings", sumSettingsRepository.findOne(sumSettingsId));

        if(depositorSettings.nonConsistentSumSettings().contains(sumSettingsId)){
            model.addAttribute("nonConsistent", true);
        }

        return "ajax/admin/depositors/percents_save";
    }
}
