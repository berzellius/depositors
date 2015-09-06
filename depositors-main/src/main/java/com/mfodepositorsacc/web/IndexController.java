package com.mfodepositorsacc.web;

/**
 * Created by berz on 20.10.14.
 */

import com.mfodepositorsacc.dmodel.User;
import com.mfodepositorsacc.dmodel.UserRole;
import com.mfodepositorsacc.scheduling.MainScheduler;
import com.mfodepositorsacc.util.UserLoginUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ViewResolver;

import java.util.Locale;

@Controller
public class IndexController {


    @Autowired
    UserLoginUtil userLoginUtil;

    @Autowired
    MainScheduler emailScheduling;


    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
    public String indexPage() throws Exception {

        User user = userLoginUtil.getCurrentLogInUser();

        if(user == null){
            return "redirect:/login";
        }

        if(userLoginUtil.userHaveRole(user, UserRole.Role.ADMIN)){
            return "redirect:/administrator";
        }

        if(userLoginUtil.userHaveRole(user, UserRole.Role.DEPOSITOR)){
            return "redirect:/depositor";
        }

        return "redirect:/login";
    }

    @RequestMapping(value = "/testpdf", method = RequestMethod.GET)
     public ModelAndView testPdf(){
        return new ModelAndView("pdfView", "test", "test");
    }

    @RequestMapping(value = "/testpdf1", method = RequestMethod.GET)
    public ModelAndView testPdf1(){
        return new ModelAndView("pdfView1", "test", "test");
    }

}
