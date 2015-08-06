package com.mfodepositorsacc.web;

import com.mfodepositorsacc.billing.BillingMainContract;
import com.mfodepositorsacc.dmodel.User;
import com.mfodepositorsacc.dmodel.UserRole;
import com.mfodepositorsacc.service.BillingSystemUtils;
import com.mfodepositorsacc.util.UserLoginUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by berz on 18.01.15.
 */
@Controller
@RequestMapping("/money_account")
public class MoneyAccountController {

    @Autowired
    BillingMainContract billingMainContract;

    @Autowired
    BillingSystemUtils billingSystemUtils;

    @Autowired
    UserLoginUtil userLoginUtil;




    @RequestMapping(value = "confirm_pay_in", method = RequestMethod.POST)
    public String confirmPayIn(
            @RequestParam(value = "code")
            String code
    ){
        User currentUser = userLoginUtil.getCurrentLogInUser();

        if(userLoginUtil.userHaveRole(currentUser, UserRole.Role.ADMIN)){
            /*try {
                billingMainContract.doDepositPayInAccountConfirm(code);
            } catch (DocumentNotFoundException e) {
                //
            }*/
        }

        return "redirect:/money_account/payin_requests";
    }

}
