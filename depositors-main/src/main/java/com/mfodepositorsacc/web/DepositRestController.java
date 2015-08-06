package com.mfodepositorsacc.web;

import com.mfodepositorsacc.dmodel.User;
import com.mfodepositorsacc.dmodel.UserRole;
import com.mfodepositorsacc.dto.AccountSaldoGraph;
import com.mfodepositorsacc.service.BillingSystemUtils;
import com.mfodepositorsacc.util.UserLoginUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;

/**
 * Created by berz on 09.07.15.
 */
@RestController
@RequestMapping(value = "/rest/depositor")
public class DepositRestController {

    @Autowired
    UserLoginUtil userLoginUtil;

    @Autowired
    BillingSystemUtils billingSystemUtils;

    @RequestMapping(value = "/graph")
    LinkedHashMap<String, AccountSaldoGraph> graph(){
        User user = userLoginUtil.getCurrentLogInUser();

        if(
                user == null ||
                        !userLoginUtil.userHaveRole(user, UserRole.Role.DEPOSITOR)
                ){
            return null;
        }

        AccountSaldoGraph accountSaldoGraph = billingSystemUtils.getAccountSaldoGraph(user.getDeposit());
        AccountSaldoGraph accountSaldoGraphAvailable = billingSystemUtils.getAccountAvailableToOutcomeSaldoGraph(user.getDeposit());

        LinkedHashMap<String, AccountSaldoGraph> graphs = new LinkedHashMap<String, AccountSaldoGraph>();
        graphs.put("all", accountSaldoGraph);
        graphs.put("available", accountSaldoGraphAvailable);

        return graphs;
    }

}
