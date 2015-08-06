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

@Controller
public class IndexController {


    @Autowired
    UserLoginUtil userLoginUtil;

    @Autowired
    MainScheduler emailScheduling;


    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
    public String indexPage(){


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

}
