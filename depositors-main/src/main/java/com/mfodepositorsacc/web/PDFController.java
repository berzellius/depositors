package com.mfodepositorsacc.web;

import com.mfodepositorsacc.settings.ProjectSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by berz on 06.09.2015.
 */
@Controller
@RequestMapping("/pdf")
public class PDFController {

    @Autowired
    ProjectSettings projectSettings;

    @RequestMapping
    public ModelAndView example(){
        HashMap<String, String> data = new LinkedHashMap<String, String>();
        data.put("title", "Pdf documents");
        data.put("intro", "Русскоязычно и по-русски. Ёж.");

        ModelAndView mav = new ModelAndView("examplePdfView", "data", data);
        mav.addObject("projectSettings", projectSettings);

        return mav;
    }

}
