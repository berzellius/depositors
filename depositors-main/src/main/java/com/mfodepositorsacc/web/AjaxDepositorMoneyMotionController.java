package com.mfodepositorsacc.web;

import com.mfodepositorsacc.accountingmachine.exception.RedSaldoException;
import com.mfodepositorsacc.billing.dmodel.DepositOutcomeDocument;
import com.mfodepositorsacc.billing.dmodel.DepositPayInAccountDocument;
import com.mfodepositorsacc.billing.exception.DocumentAlreadyProcessingException;
import com.mfodepositorsacc.billing.exception.DocumentNotFoundException;
import com.mfodepositorsacc.billing.exception.IllegalDocumentStateException;
import com.mfodepositorsacc.dmodel.User;
import com.mfodepositorsacc.dmodel.UserRole;
import com.mfodepositorsacc.dto.AccountSaldoGraph;
import com.mfodepositorsacc.service.BillingSystemUtils;
import com.mfodepositorsacc.service.DepositService;
import com.mfodepositorsacc.util.UserLoginUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

/**
 * Created by berz on 28.06.15.
 */
@Controller
@RequestMapping("ajax/depositor/moneymotion")
public class AjaxDepositorMoneyMotionController extends BaseController {

    @Autowired
    UserLoginUtil userLoginUtil;

    @Autowired
    DepositService depositService;

    @Autowired
    BillingSystemUtils billingSystemUtils;

    /*
    *   Методы обработки запросов по зачислению средств на счет.
     */
    @RequestMapping(value = "/payinrequest", method = {
            RequestMethod.GET
    })
    public String getMoneyMotionPayIn(
                Model model,
                @RequestParam(value = "showAddForm", required = false)
                Boolean showAddForm
            ){
        User user = userLoginUtil.getCurrentLogInUser();

        if(
                user == null ||
                        !userLoginUtil.userHaveRole(user, UserRole.Role.DEPOSITOR)
                ){
            return null;
        }

        model.addAttribute("log", billingSystemUtils.getPayInAccountRequestsUndoneLog(user.getDeposit()));
        model.addAttribute("showAddForm", (showAddForm != null)? showAddForm : true);

        return "ajax/depositors/moneymotion/payin";
    }

    @RequestMapping(value = "/payinrequest", method = RequestMethod.POST)
    public String moneyMotionPayIn(
            Model model,
            @RequestParam(value = "sum")
            BigDecimal sum,
            final RedirectAttributes redirectAttributes
    ){
        User user = userLoginUtil.getCurrentLogInUser();

        if(
                user == null ||
                        !userLoginUtil.userHaveRole(user, UserRole.Role.DEPOSITOR)
                ){
            return null;
        }

        DepositPayInAccountDocument depositPayInAccountDocument = depositService.requestAdditionalMoneyPayIn(user.getDeposit(), sum);
        //model.addAttribute("log", billingSystemUtils.getPayInAccountRequestsUndoneLog(user.getDeposit()));

        //return "ajax/depositors/moneymotion/payin";
        return "redirect:/ajax/depositor/moneymotion/payinrequest";
    }

    @RequestMapping(value = "/payinrequest", method = RequestMethod.DELETE)
    public ModelAndView moneyMotionCancelPayIn(
            Model model,
            @RequestParam(value = "code")
            String code,
            HttpServletResponse httpServletResponse,
            final RedirectAttributes redirectAttributes
    ){
        User user = userLoginUtil.getCurrentLogInUser();

        if(
                user == null ||
                        !userLoginUtil.userHaveRole(user, UserRole.Role.DEPOSITOR)
                ){
            return null;
        }

        try {
            depositService.cancelRequestToPayInByDocNumber(code);
        } catch (DocumentNotFoundException e) {
            //
        } catch (DocumentAlreadyProcessingException e) {
            //
        } catch (RedSaldoException e) {
            // Это не бизнес-кейс, так как деньги ниоткуда не забирают
        } catch (IllegalDocumentStateException e) {
            redirectAttributes.addFlashAttribute("reason", e);
        }

        //model.addAttribute("log", billingSystemUtils.getPayInAccountRequestsUndoneLog(user.getDeposit()));
        redirectAttributes.addAttribute("showAddForm", false);

        /*
        * Необходимо использовать код 303 для редиректа, чтобы браузеры использовали GET-метод
         */
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("/ajax/depositor/moneymotion/payinrequest");
        redirectView.setStatusCode(HttpStatus.SEE_OTHER);

        ModelAndView modelAndView = new ModelAndView(redirectView);

        return modelAndView;
    }

    /*
    *   Методы для работы с запросами на вывод средств
     */
    @RequestMapping(value = "/outcomerequest", method = RequestMethod.GET)
    public String getMoneyMotionOutcome(
            Model model,
            @RequestParam(value = "showAddForm", required = false)
            Boolean showAddForm,
            @RequestParam(value = "redSaldo", required = false)
            Boolean redSaldo
    ){
        User user = userLoginUtil.getCurrentLogInUser();

        if(
                user == null ||
                        !userLoginUtil.userHaveRole(user, UserRole.Role.DEPOSITOR)
                ){
            return null;
        }

        model.addAttribute("log", billingSystemUtils.getOutcomeUndoneLog(user.getDeposit()));
        model.addAttribute("showAddForm", (showAddForm != null)? showAddForm : true);
        model.addAttribute("redSaldo", (redSaldo != null)? redSaldo : false);

        return "ajax/depositors/moneymotion/outcome";
    }

    @RequestMapping(value = "/outcomerequest", method = RequestMethod.POST)
    public String moneyMotionOutcome(
            Model model,
            @RequestParam(value = "sum")
            BigDecimal sum,
            final RedirectAttributes redirectAttributes
    ){
        User user = userLoginUtil.getCurrentLogInUser();

        if(
                user == null ||
                        !userLoginUtil.userHaveRole(user, UserRole.Role.DEPOSITOR)
                ){
            return null;
        }

        try {
            DepositOutcomeDocument depositOutcomeDocument = depositService.requestEarlyOutcome(user.getDeposit(), sum);
        } catch (RedSaldoException e) {
            // Нет столько денег на счету
            redirectAttributes.addAttribute("redSaldo", true);
        }

        return "redirect:/ajax/depositor/moneymotion/outcomerequest";
    }

    @RequestMapping(value = "/outcomerequest", method = RequestMethod.DELETE)
    public ModelAndView moneyMotionCancelOutcome(
            Model model,
            @RequestParam(value = "code")
            String code,
            HttpServletResponse httpServletResponse,
            final RedirectAttributes redirectAttributes
    ){
        User user = userLoginUtil.getCurrentLogInUser();

        if(
                user == null ||
                        !userLoginUtil.userHaveRole(user, UserRole.Role.DEPOSITOR)
                ){
            return null;
        }

        try {
            depositService.cancelRequestToOutcomeByDocNumber(code);
        } catch (DocumentNotFoundException e) {
            //
        } catch (DocumentAlreadyProcessingException e) {
            //
        } catch (RedSaldoException e) {
            redirectAttributes.addAttribute("redSaldo", true);
        } catch (IllegalDocumentStateException e) {
            redirectAttributes.addFlashAttribute("reason", e);
        }

        redirectAttributes.addAttribute("showAddForm", false);

        /*
        * Необходимо использовать код 303 для редиректа, чтобы браузеры использовали GET-метод
         */
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("/ajax/depositor/moneymotion/outcomerequest");
        redirectView.setStatusCode(HttpStatus.SEE_OTHER);

        ModelAndView modelAndView = new ModelAndView(redirectView);

        return modelAndView;
    }


}
