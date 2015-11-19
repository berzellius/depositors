package com.mfodepositorsacc.web;

import com.mfodepositorsacc.dmodel.*;
import com.mfodepositorsacc.maincontract.MainContract;
import com.mfodepositorsacc.service.*;
import com.mfodepositorsacc.settings.ProjectSettings;
import com.mfodepositorsacc.util.UserLoginUtil;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by berz on 13.05.15.
 */
@Controller
@RequestMapping(value = "/depositor")
public class DepositorController {

    @Autowired
    ReadJsonFiles readJsonFiles;

    @Autowired
    UserLoginUtil userLoginUtil;

    @Autowired
    DepositCalculationService depositCalculationService;

    @Autowired
    BillingSystemUtils billingSystemUtils;

    @Autowired
    DepositService depositService;

    @Autowired
    NewsService newsService;

    @Autowired
    DepositorService depositorService;

    @Autowired
    ProjectSettings projectSettings;

    @RequestMapping
    public String index(
            Model model
    ){
        User user = userLoginUtil.getCurrentLogInUser();

        if(
                user == null ||
                        !userLoginUtil.userHaveRole(user, UserRole.Role.DEPOSITOR)
                ){
            return "redirect:/";
        }

        BigDecimal lockedOutcomeSaldo = billingSystemUtils.getDepositLockedToOutcomeSaldo(user.getDeposit().getId());

        model.addAttribute("user", user);
        model.addAttribute("deposit", user.getDeposit());
        model.addAttribute("basepayinmustbecleared", depositCalculationService.getPayInBillMustBeCleared(user.getDeposit()));
        model.addAttribute("saldo", billingSystemUtils.getDepositSaldo(user.getDeposit().getId()));
        model.addAttribute("availableSaldo", billingSystemUtils.getDepositAvailableSaldo(user.getDeposit().getId()));
        model.addAttribute("lockedOutcomeSaldo", lockedOutcomeSaldo.compareTo(BigDecimal.ZERO) == 1 ? lockedOutcomeSaldo : null);
        model.addAttribute("marketing", depositCalculationService.calculteDepositMarketingData(user.getDeposit(), user.getDeposit().getSum()));
        model.addAttribute("moneymotionlogs", depositService.getMoneyMotionLogs(user.getDeposit()));
        model.addAttribute("news", newsService.newsItemsByManagedUnitsPublished(user.getManagedUnits()));
        model.addAttribute("documents2download", depositorService.getDepositorDocumentsReady(user.getDeposit()));
        //System.out.println("news: " + newsService.newsItemsByManagedUnits(user.getManagedUnits()));

        return "depositors/index";
    }

    @RequestMapping(value = "documents/{type}/download")
    public void getUploadedFile(
            @PathVariable(value = "type")
            DepositorDocument.Type type,
            HttpServletResponse response,
            HttpServletRequest request
    ) throws NotFoundException {
        User user = userLoginUtil.getCurrentLogInUser();

        if(
                user == null ||
                        !userLoginUtil.userHaveRole(user, UserRole.Role.DEPOSITOR)
                ){
            return;
        }

        HashMap<DepositorDocument.Type, DepositorDocument> docs = depositorService.getDepositorDocumentsReady(user.getDeposit());

        for(DepositorDocument depositorDocument : docs.values()){
            System.out.println(depositorDocument.getType().getAlias());
        }

        DepositorDocument doc = docs.get(type);

        if(doc == null){
            System.out.println("doc not found");
            throw new NotFoundException("document not found");
        }

        UploadedFile uploadedFile = doc.getUploadedFile();

        if(uploadedFile == null)
            throw new NotFoundException("file not found");

        File file = null;
        try {
            file = new File(projectSettings.getPathToUploads().concat("/").concat(uploadedFile.getPath()));

            response.setCharacterEncoding("UTF-8");

            if(uploadedFile.getMimeType() != null)
                response.setHeader("Content-Type", uploadedFile.getMimeType().concat("; charset=UTF-8"));

            response.setHeader("Content-Disposition",
                    "attachment; filename=\""
                            .concat("deposit_".concat(user.getDeposit().getId().toString()).concat("_"))
                            .concat(type.getAlias().concat("_"))
                            .concat(uploadedFile.getFilename())
                            .concat(".")
                            .concat(uploadedFile.getExtension())
                            .concat("\""));

            InputStream inputStream = new FileInputStream(file);
            FileCopyUtils.copy(inputStream, response.getOutputStream());
        } catch (FileNotFoundException e) {
            throw new NotFoundException("file not found!");
        } catch (IOException e) {
            throw new NotFoundException("file not found!");
        }
    }

    @RequestMapping(value = "/moneymotion")
    public String moneyMotion(
            Model model
    ){
        User user = userLoginUtil.getCurrentLogInUser();

        if(
                user == null ||
                        !userLoginUtil.userHaveRole(user, UserRole.Role.DEPOSITOR)
                ){
            return "redirect:/";
        }

        if(depositCalculationService.getPayInBillMustBeCleared(user.getDeposit()) != null){
            return "redirect:/depositor/";
        }

        model.addAttribute("user", user);
        model.addAttribute("deposit", user.getDeposit());
        model.addAttribute("moneymotionlogs", depositService.getMoneyMotionRequestsLogs(user.getDeposit()));
        model.addAttribute("saldo", billingSystemUtils.getDepositSaldo(user.getDeposit().getId()));

        return "depositors/moneymotion";
    }

    @RequestMapping(value = "/mainContract")
    public ModelAndView mainContract(
        @RequestParam(value = "print", required = false)
        Boolean print
    ){
        MainContract mainContract = null;
        User user = userLoginUtil.getCurrentLogInUser();

        if(
                user == null ||
                        !userLoginUtil.userHaveRole(user, UserRole.Role.DEPOSITOR)
                ){
            return null;
        }
        try {
            mainContract = readJsonFiles.readMainContract();
            Deposit deposit = user.getDeposit();
            HashMap<String, Object> data = new LinkedHashMap<String, Object>();
            data.put("headerSettings", mainContract.getHeaderSettings());
            data.put("htmlBlocks", mainContract.getHtmlBlocks());

            data.put("placeholders", depositService.mainContractPdfPlaceholders(deposit));
            ModelAndView mav = new ModelAndView("mainContractPdfView", "data", data);
            return mav;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}
