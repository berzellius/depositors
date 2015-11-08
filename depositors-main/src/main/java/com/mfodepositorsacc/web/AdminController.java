package com.mfodepositorsacc.web;

import com.mfodepositorsacc.dmodel.User;
import com.mfodepositorsacc.dmodel.UserRole;
import com.mfodepositorsacc.repository.DepositorRepository;
import com.mfodepositorsacc.repository.DepositorTypeSettingsRepository;
import com.mfodepositorsacc.repository.NotificationRepository;
import com.mfodepositorsacc.repository.UserRepository;
import com.mfodepositorsacc.service.DepositorService;
import com.mfodepositorsacc.service.DepositorSettings;
import com.mfodepositorsacc.specifications.NotificationSpecifications;
import com.mfodepositorsacc.util.UserLoginUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by berz on 17.05.15.
 */
@Controller
@RequestMapping(value = "/administrator")
public class AdminController extends BaseController {

    @Autowired
    UserLoginUtil userLoginUtil;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DepositorRepository depositorRepository;

    @Autowired
    DepositorTypeSettingsRepository depositorTypeSettingsRepository;

    @Autowired
    DepositorSettings depositorSettings;

    @Autowired
    DepositorService depositorService;

    @Autowired
    NotificationRepository notificationRepository;

    private boolean isAdmin(User user){
        return
                user != null &&
                userLoginUtil.userHaveRole(user, UserRole.Role.ADMIN);
    }

    @RequestMapping
    public String index(
            Model model
    ){
        User user = userLoginUtil.getCurrentLogInUser();

        if(!isAdmin(user)){
            return "redirect:/";
        }

        model.addAttribute("depositorsAll", depositorRepository.count());
        model.addAttribute("depositorsNonConfirmed", userLoginUtil.countNonActivatedRegistration());
        model.addAttribute("depositorsEmptySum", 0);
        model.addAttribute("depositorsEmailNotSent", notificationRepository.count(NotificationSpecifications.waitingRegistrationConfirm()));

        Pageable pageableNonActivatedRegistration = new PageRequest(0, 30);
        Page<User> nonActivatedRegistration = userRepository.findAll(userLoginUtil.getNonConfirmedRegistrationSpecification(), pageableNonActivatedRegistration);

        model.addAttribute("nonActivatedRegistrationUsers", nonActivatedRegistration);


        return "administrator/index";
    }

    @RequestMapping(value = "/depsettings")
    public String depositorTypeSettings(
            Model model
    ){
        model.addAttribute("depositorTypeSettingsList", depositorTypeSettingsRepository.findAll());
        model.addAttribute("nonConsistentSumSettings", depositorSettings.nonConsistentSumSettings());
        model.addAttribute("nonConsistentDepositorTypeSettings", depositorSettings.nonConsistentDepositorTypeSettings());

        return "administrator/depositorTypeSettings";
    }

}
