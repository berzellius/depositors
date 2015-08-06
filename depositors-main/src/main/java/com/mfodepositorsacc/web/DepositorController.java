package com.mfodepositorsacc.web;

import com.mfodepositorsacc.dmodel.User;
import com.mfodepositorsacc.dmodel.UserRole;
import com.mfodepositorsacc.service.BillingSystemUtils;
import com.mfodepositorsacc.service.DepositCalculationService;
import com.mfodepositorsacc.service.DepositService;
import com.mfodepositorsacc.util.UserLoginUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * Created by berz on 13.05.15.
 */
@Controller
@RequestMapping(value = "/depositor")
public class DepositorController {

    @Autowired
    UserLoginUtil userLoginUtil;

    @Autowired
    DepositCalculationService depositCalculationService;

    @Autowired
    BillingSystemUtils billingSystemUtils;

    @Autowired
    DepositService depositService;

    @RequestMapping
    public String index(
            Model model
    ){
        User user = userLoginUtil.getCurrentLogInUser();

        if(
                user == null ||
                        !userLoginUtil.userHaveRole(user, UserRole.Role.DEPOSITOR)
                ){
            return "redirect:/";
        }

        BigDecimal lockedOutcomeSaldo = billingSystemUtils.getDepositLockedToOutcomeSaldo(user.getDeposit().getId());

        model.addAttribute("user", user);
        model.addAttribute("deposit", user.getDeposit());
        model.addAttribute("basepayinmustbecleared", depositCalculationService.getPayInBillMustBeCleared(user.getDeposit()));
        model.addAttribute("saldo", billingSystemUtils.getDepositSaldo(user.getDeposit().getId()));
        model.addAttribute("availableSaldo", billingSystemUtils.getDepositAvailableSaldo(user.getDeposit().getId()));
        model.addAttribute("lockedOutcomeSaldo", lockedOutcomeSaldo.compareTo(BigDecimal.ZERO) == 1 ? lockedOutcomeSaldo : null);
        model.addAttribute("marketing", depositCalculationService.calculteDepositMarketingData(user.getDeposit(), user.getDeposit().getSum()));
        model.addAttribute("moneymotionlogs", depositService.getMoneyMotionLogs(user.getDeposit()));

        return "depositors/index";
    }

    @RequestMapping(value = "/moneymotion")
    public String moneyMotion(
            Model model
    ){
        User user = userLoginUtil.getCurrentLogInUser();

        if(
                user == null ||
                        !userLoginUtil.userHaveRole(user, UserRole.Role.DEPOSITOR)
                ){
            return "redirect:/";
        }

        if(depositCalculationService.getPayInBillMustBeCleared(user.getDeposit()) != null){
            return "redirect:/depositor/";
        }

        model.addAttribute("user", user);
        model.addAttribute("deposit", user.getDeposit());
        model.addAttribute("moneymotionlogs", depositService.getMoneyMotionRequestsLogs(user.getDeposit()));
        model.addAttribute("saldo", billingSystemUtils.getDepositSaldo(user.getDeposit().getId()));

        return "depositors/moneymotion";
    }
}
