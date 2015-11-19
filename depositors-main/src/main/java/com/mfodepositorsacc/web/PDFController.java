package com.mfodepositorsacc.web;

import com.mfodepositorsacc.maincontract.MainContract;
import com.mfodepositorsacc.service.ReadJsonFiles;
import com.mfodepositorsacc.settings.ProjectSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
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

    @Autowired
    ReadJsonFiles readJsonFiles;

    @RequestMapping
    public ModelAndView example(){


        try {
            MainContract mainContract = readJsonFiles.readMainContract();
            HashMap<String, Object> data = new LinkedHashMap<String, Object>();
            data.put("headerSettings", mainContract.getHeaderSettings());
            data.put("htmlBlocks", mainContract.getHtmlBlocks());
            HashMap<String, Object> placeholders = new LinkedHashMap<String, Object>();
            placeholders.put("$CONTRACT_NUMBER", 123);
            placeholders.put("$SOME_VAR", "переменная");

            data.put("placeholders", placeholders);
            ModelAndView mav = new ModelAndView("examplePdfView", "data", data);

            mav.addObject("projectSettings", projectSettings);
            return mav;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

}
