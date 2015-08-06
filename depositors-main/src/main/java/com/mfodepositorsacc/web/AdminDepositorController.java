package com.mfodepositorsacc.web;

import com.mfodepositorsacc.dmodel.Deposit;
import com.mfodepositorsacc.dmodel.Depositor;
import com.mfodepositorsacc.dmodel.User;
import com.mfodepositorsacc.dmodel.UserRole;
import com.mfodepositorsacc.repository.UserRepository;
import com.mfodepositorsacc.repository.UserRoleRepository;
import com.mfodepositorsacc.service.BillingSystemUtils;
import com.mfodepositorsacc.service.DepositCalculationService;
import com.mfodepositorsacc.service.DepositService;
import com.mfodepositorsacc.specifications.UserSpecifications;
import com.mfodepositorsacc.wrappers.PageWrapper;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

/**
 * Created by berz on 11.07.15.
 */
@Controller
@RequestMapping(value = "/administrator/depositor/")
public class AdminDepositorController extends BaseController {

    @Autowired
    BillingSystemUtils billingSystemUtils;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    DepositCalculationService depositCalculationService;

    @Autowired
    DepositService depositService;

    @RequestMapping(value = "/cardbydeposit")
    public String cardByDepositor(
            @RequestParam(value = "depositor")
            Depositor depositor,
            Model model,
            final RedirectAttributes redirectAttributes
    ) throws NotFoundException {
        User user = (User) userRepository.findOne(
                Specifications.where(
                        UserSpecifications.byDepositor(depositor)
                )
                        .and(
                                UserSpecifications.hasRole(userRoleRepository.findOneByRole(UserRole.Role.DEPOSITOR))
                        )
        );

        if (user == null) {
            throw new NotFoundException("user not found!");
        }

        redirectAttributes.addAttribute("user", user.getId());

        return "redirect:/administrator/depositor/card";
    }

    @RequestMapping(value = "/card")
    public String card(
            @RequestParam(value = "user")
            User user,
            Model model
    ) throws NotFoundException {
        if (user == null) {
            throw new NotFoundException("user not found!");
        }

        BigDecimal lockedOutcomeSaldo = billingSystemUtils.getDepositLockedToOutcomeSaldo(user.getDeposit().getId());

        model.addAttribute("user", user);
        model.addAttribute("deposit", user.getDeposit());
        model.addAttribute("basepayinmustbecleared", depositCalculationService.getPayInBillMustBeCleared(user.getDeposit()));
        model.addAttribute("saldo", billingSystemUtils.getDepositSaldo(user.getDeposit().getId()));
        model.addAttribute("availableSaldo", billingSystemUtils.getDepositAvailableSaldo(user.getDeposit().getId()));
        model.addAttribute("lockedOutcomeSaldo", lockedOutcomeSaldo.compareTo(BigDecimal.ZERO) == 1 ? lockedOutcomeSaldo : null);
        model.addAttribute("moneymotionlogs", depositService.getMoneyMotionLogs(user.getDeposit()));

        return "administrator/depositors/card";
    }

    @RequestMapping(value = "/list")
    public String depositors(
            Model model,
            @PageableDefault(page = 0, value = 20)
            Pageable pageable,
            @RequestParam(value = "deposit", required = false)
            Deposit deposit
    ) {

        Page<User> userDepositors;
        if (deposit == null) {
            userDepositors = userRepository.findAll(
                    Specifications.where(UserSpecifications.hasRole(
                            userRoleRepository.findOneByRole(UserRole.Role.DEPOSITOR)
                    )),
                    pageable
            );
        }
        else{
            userDepositors = userRepository.findAllByDeposit(deposit, pageable);
        }

        model.addAttribute("depositors", userDepositors);

        PageWrapper<User> pageWrapper = new PageWrapper<User>(userDepositors, "/administrator/depositor/list");
        model.addAttribute("page", pageWrapper);

        return "administrator/depositors/list";
    }

}
