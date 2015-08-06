package com.mfodepositorsacc.web;

import com.mfodepositorsacc.service.BillingSystemUtils;
import com.mfodepositorsacc.util.MoneyMotionRowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by berz on 21.06.15.
 */
@Controller
@RequestMapping(value = "/administrator/moneymotion")
public class AdminMoneyMotionController extends BaseController {

    @Autowired
    BillingSystemUtils billingSystemUtils;

    @Autowired
    MoneyMotionRowUtil moneyMotionRowUtil;

    @RequestMapping(value = "/")
    public String index(
            Model model
    ){
        model.addAttribute("mainSumCount", billingSystemUtils.countNewBasePayInRequests());
        model.addAttribute("addtSumCount", billingSystemUtils.countNewAdditionalPayInRequests());
        model.addAttribute("outComeCount", billingSystemUtils.countNewEarlyOutcomeRequests());
        model.addAttribute("closingOutcomeCount", 0);

        Pageable pageable = new PageRequest(0, 50);

        model.addAttribute("mainSumDocs", moneyMotionRowUtil.toList(billingSystemUtils.newBasePayInRequests(pageable)));
        model.addAttribute("addtSumDocs", moneyMotionRowUtil.toList(billingSystemUtils.newAdditionalPayInRequests(pageable)));
        model.addAttribute("outcomeDocs", moneyMotionRowUtil.toList(billingSystemUtils.newEarlyOutcomeRequests(pageable)));

        return "administrator/moneymotion/index";
    }

}
