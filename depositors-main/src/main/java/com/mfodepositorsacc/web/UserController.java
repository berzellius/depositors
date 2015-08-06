package com.mfodepositorsacc.web;

import com.mfodepositorsacc.dmodel.Deposit;
import com.mfodepositorsacc.dmodel.User;
import com.mfodepositorsacc.dmodel.UserRole;
import com.mfodepositorsacc.exceptions.WrongInputDataException;
import com.mfodepositorsacc.repository.UserRepository;
import com.mfodepositorsacc.repository.UserRoleRepository;
import com.mfodepositorsacc.util.UserLoginUtil;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;

/**
 * Created by berz on 08.11.14.
 */
@Controller
@RequestMapping(value = "/users")
public class UserController extends BaseController {


    @Autowired
    UserRepository userRepository;

    @Autowired
    UserLoginUtil userLoginUtil;

    @Autowired
    UserRoleRepository userRoleRepository;




    /*
    *
    * Редирект в зависимости от роли
     */
    private String redirectByRole(Set<UserRole> authorities) {


        return "redirect:/";
    }

    /*
    *
    * Проверка доступа
     */
    private boolean accessCheck(User user, User currentUser) {


        return true;
    }

    @RequestMapping("/activation")
    public String activationPage(
            @RequestParam(value = "code")
            String code,
            Model model
    ) throws NotFoundException {
        User activatingUser = userRepository.findByActivationCode(code);

        if (activatingUser == null) {
            throw new NotFoundException("Document not found");
        }

        model.addAttribute("user", activatingUser);

        return "users/activation";
    }

    @RequestMapping(value = "/activation", method = RequestMethod.POST)
    public String activate(
            @RequestParam(value = "code")
            String code,
            @RequestParam(value = "password")
            String password,
            @RequestParam(value = "passwordConfirm")
            String passwordConfirm,
            Model model,
            final RedirectAttributes redirectAttributes
    ){


        try {
            userLoginUtil.activateUser(code, password, passwordConfirm);
            return "redirect:/login";
        } catch (WrongInputDataException e) {
            redirectAttributes.addFlashAttribute("reason", e.getReason());
            return "redirect:/users/activation?code=".concat(code);
        }

    }

    @RequestMapping(value = "forgot_password")
    public String forgotPassword(
            Model model
    ){

        return "users/forgot_password";
    }

    @RequestMapping(value = "forgot_password", method = RequestMethod.POST)
    public String forgotPasswordReq(
            Model model,
            @RequestParam(value = "deposit_id")
            Deposit deposit
    ){

        try {
            userLoginUtil.requestToRestorePassword(deposit);
        } catch (WrongInputDataException e) {
            //e.printStackTrace();
        }

        return "users/forgot_password";
    }

    @RequestMapping("/restore_pass_link")
    public String restorePassPage(
            @RequestParam(value = "code")
            String code,
            Model model
    ) throws NotFoundException {
        User activatingUser = userRepository.findByActivationCode(code);

        if (activatingUser == null) {
            throw new NotFoundException("Document not found");
        }

        model.addAttribute("user", activatingUser);
        model.addAttribute("mode", "restorePass");

        return "users/activation";
    }

    @RequestMapping(value = "/restore_pass_link", method = RequestMethod.POST)
    public String restorePass(
            @RequestParam(value = "code")
            String code,
            @RequestParam(value = "password")
            String password,
            @RequestParam(value = "passwordConfirm")
            String passwordConfirm,
            Model model,
            final RedirectAttributes redirectAttributes
    ){


        try {
            userLoginUtil.restorePassword(code, password, passwordConfirm);
            return "redirect:/login";
        } catch (WrongInputDataException e) {
            redirectAttributes.addFlashAttribute("reason", e.getReason());
            return "redirect:/users/activation?code=".concat(code);
        }

    }
}
