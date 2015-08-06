package com.mfodepositorsacc.web;

import com.mfodepositorsacc.dmodel.Deposit;
import com.mfodepositorsacc.dmodel.Depositor;
import com.mfodepositorsacc.exceptions.WrongInputDataException;
import com.mfodepositorsacc.repository.DepositRepository;
import com.mfodepositorsacc.repository.DepositorTypeSettingsRepository;
import com.mfodepositorsacc.service.DepositCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Created by berz on 24.04.15.
 */
@Controller
@RequestMapping("/signup")
public class SignUpController {

    @Autowired
    DepositorTypeSettingsRepository depositorTypeSettingsRepository;

    @Autowired
    DepositCalculationService depositCalculationService;

    @Autowired
    DepositRepository depositRepository;

    @RequestMapping
    public String main(
            Model model
    ) {
        return "users/signup";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String signupAction(
            Model model,
            final RedirectAttributes redirectAttributes,
            Deposit deposit,
            Depositor depositor
    ) {

        try {
            depositCalculationService.processRegistration(deposit, depositor);
        } catch (WrongInputDataException e) {
            redirectAttributes.addFlashAttribute("reason", e.getReason());
            return "redirect:/signup";
        }


        return "users/endsignup";
    }


}
