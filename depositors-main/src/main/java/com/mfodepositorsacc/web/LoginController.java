package com.mfodepositorsacc.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by berz on 26.10.14.
 */
@Controller
public class LoginController {

    @RequestMapping("/login")
    public String loginPage(){
        return "login";
    }


}
