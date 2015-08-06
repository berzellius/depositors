package com.mfodepositorsacc.web;

import com.mfodepositorsacc.dmodel.Deposit;
import com.mfodepositorsacc.dmodel.User;
import com.mfodepositorsacc.service.DepositService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

/**
 * Created by berz on 12.07.15.
 */
@Controller
@RequestMapping(value = "/administrator/deposit")
public class AdminDepositController extends BaseController {

    @Autowired
    DepositService depositService;

    @RequestMapping(value = "/percent", method = RequestMethod.POST)
    public String percent(
            @RequestParam(value = "deposit")
            Deposit deposit,
            @RequestParam(value = "user")
            User user,
            @RequestParam(value = "percent")
            BigDecimal percent,
            final RedirectAttributes redirectAttributes
    ) throws NotFoundException {
        if(deposit == null || user == null){
            throw new NotFoundException("not found depositor!");
        }

        depositService.updatePercent(deposit, percent);

        redirectAttributes.addAttribute("user", user.getId());
        return "redirect:/administrator/depositor/card";
    }

}
