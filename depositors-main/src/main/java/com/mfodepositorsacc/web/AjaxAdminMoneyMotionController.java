package com.mfodepositorsacc.web;

import com.mfodepositorsacc.accountingmachine.exception.RedSaldoException;
import com.mfodepositorsacc.billing.exception.DocumentNotFoundException;
import com.mfodepositorsacc.billing.exception.IllegalDocumentStateException;
import com.mfodepositorsacc.dto.MoneyMotionRow;
import com.mfodepositorsacc.service.BillingSystemUtils;
import com.mfodepositorsacc.util.MoneyMotionRowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Created by berz on 21.06.15.
 */
@Controller
@RequestMapping(value = "/ajax/admin/moneymotion")
public class AjaxAdminMoneyMotionController extends BaseController {

    @Autowired
    BillingSystemUtils billingSystemUtils;

    @Autowired
    MoneyMotionRowUtil moneyMotionRowUtil;

    @RequestMapping(value = "/mainsumpayin", method = RequestMethod.GET)
    public String getMainSumPayIn(
            Model model
    ){
        Pageable pageable = new PageRequest(0, 50);

        List<MoneyMotionRow> moneyMotionRows = moneyMotionRowUtil.toList(billingSystemUtils.newBasePayInRequests(pageable));
        model.addAttribute("mainSumDocs", moneyMotionRows);

        return "ajax/admin/moneymotion/mainsumpayin";
    }

    @RequestMapping(value = "/mainsumpayin", method = RequestMethod.POST)
    public String mainSumPayIn(
            Model model,
            @RequestParam(value = "doc_num")
            String docNum,
            final RedirectAttributes redirectAttributes
    ){
        try {
            billingSystemUtils.doDepositPayInAccountConfirmByDocNumber(docNum);
        } catch (DocumentNotFoundException e) {
            redirectAttributes.addFlashAttribute("reason", e);
        } catch (IllegalDocumentStateException e) {
            redirectAttributes.addFlashAttribute("reason", e);
        }

        return "redirect:/ajax/admin/moneymotion/mainsumpayin";
    }

    @RequestMapping(value = "/addtsumpayin", method = RequestMethod.GET)
    public String getAdditionalSumPayIn(
            Model model
    ){
        Pageable pageable = new PageRequest(0, 50);

        List<MoneyMotionRow> moneyMotionRows = moneyMotionRowUtil.toList(billingSystemUtils.newAdditionalPayInRequests(pageable));
        model.addAttribute("addtSumDocs", moneyMotionRows);

        return "ajax/admin/moneymotion/addtsumpayin";
    }

    @RequestMapping(value = "/addtsumpayin", method = RequestMethod.POST)
    public String additionalSumPayIn(
            Model model,
            @RequestParam(value = "doc_num")
            String docNum,
            final RedirectAttributes redirectAttributes
    ){
        try {
            billingSystemUtils.doDepositPayInAccountConfirmByDocNumber(docNum);
        } catch (DocumentNotFoundException e) {
            redirectAttributes.addFlashAttribute("reason", e);
        } catch (IllegalDocumentStateException e) {
            redirectAttributes.addFlashAttribute("reason", e);
        }

        return "redirect:/ajax/admin/moneymotion/addtsumpayin";
    }

    @RequestMapping(value = "/outcome", method = RequestMethod.GET)
    public String getOutcome(
        Model model
    ){
        Pageable pageable = new PageRequest(0, 50);

        List<MoneyMotionRow> moneyMotionRows = moneyMotionRowUtil.toList(billingSystemUtils.newEarlyOutcomeRequests(pageable));
        model.addAttribute("outcomeDocs", moneyMotionRows);

        return "ajax/admin/moneymotion/outcome";
    }

    @RequestMapping(value = "/outcome", method = RequestMethod.POST)
    public String outcome(
            @RequestParam(value = "doc_num")
            String docNum,
            final RedirectAttributes redirectAttributes
    ){
        try {
            billingSystemUtils.doDepositOutcomeConfirmByDocNumber(docNum);
        } catch (DocumentNotFoundException e) {
            redirectAttributes.addFlashAttribute("reason", e);
        } catch (RedSaldoException e) {
            redirectAttributes.addFlashAttribute("reason", e);
        } catch (IllegalDocumentStateException e) {
            redirectAttributes.addFlashAttribute("reason", e);
        }

        return "redirect:/ajax/admin/moneymotion/outcome";
    }

}
