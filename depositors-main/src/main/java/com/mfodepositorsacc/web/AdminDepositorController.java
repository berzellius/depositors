package com.mfodepositorsacc.web;

import com.mfodepositorsacc.dmodel.*;
import com.mfodepositorsacc.exceptions.UploadFileException;
import com.mfodepositorsacc.exceptions.WrongInputDataException;
import com.mfodepositorsacc.repository.UserRepository;
import com.mfodepositorsacc.repository.UserRoleRepository;
import com.mfodepositorsacc.service.*;
import com.mfodepositorsacc.settings.ProjectSettings;
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
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

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

    @Autowired
    NewsService newsService;

    @Autowired
    DepositorService depositorService;

    @Autowired
    ProjectSettings projectSettings;

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
        model.addAttribute("news", newsService.newsItemsByManagedUnits(user.getManagedUnits()));

        model.addAttribute("documents2download", depositorService.getDepositorDocumentsReady(user.getDeposit()));

        return "administrator/depositors/card";
    }

    @RequestMapping(value = "/card/newsitem/unpublish")
    public String unpublishNewsItemFromCard(
            @RequestParam(value = "user")
            User user,
            @RequestParam(value = "newsItem")
            NewsItem newsItem,
            final RedirectAttributes redirectAttributes,
            Model model
    ){
        newsService.unpublish(newsItem);

        redirectAttributes.addAttribute("user", user.getId());
        return "redirect:/administrator/depositor/card";
    }

    @RequestMapping(value = "/card/newsitem/publish")
    public String publishNewsItemFromCard(
            @RequestParam(value = "user")
            User user,
            @RequestParam(value = "newsItem")
            NewsItem newsItem,
            final RedirectAttributes redirectAttributes,
            Model model
    ){
        newsService.publish(newsItem);

        redirectAttributes.addAttribute("user", user.getId());
        return "redirect:/administrator/depositor/card";
    }

    @RequestMapping(value = "/card/newsitem/remove", method = RequestMethod.DELETE)
    public String deleteNewsItemFromCard(
            @RequestParam(value = "user")
            User user,
            @RequestParam(value = "newsItem")
            NewsItem newsItem,
            final RedirectAttributes redirectAttributes,
            Model model
    ){
        newsService.delete(newsItem);

        redirectAttributes.addAttribute("user", user.getId());
        return "redirect:/administrator/depositor/card";
    }

    @RequestMapping(
            value = "/card/newsitem/{id}",
            method = RequestMethod.PUT)
    public String updateNewsItem(
            Model model,
            @PathVariable(value = "id")
            NewsItem newsItem,
            NewsItem newsItemModified,
            @RequestParam(value = "user")
            User user,
            final RedirectAttributes redirectAttributes
    ){
        try {
            newsService.updateNewsItem(newsItem, newsItemModified);
        } catch (WrongInputDataException e) {
            redirectAttributes.addFlashAttribute("reason", e.getReason());
            redirectAttributes.addFlashAttribute("newsItemReason", newsItem.getId());
        }

        redirectAttributes.addAttribute("user", user.getId());
        return "redirect:/administrator/depositor/card";
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

    @RequestMapping("{user}/documents")
    public String documents(
            @PathVariable(value = "user")
            User user,
            Model model
    ){
        model.addAttribute("document_types", DepositorDocument.Type.values());

        return "administrator/depositors/documents_list";
    }

    @RequestMapping("{user}/documents/{type}")
    public String documentsByType(
            @PathVariable(value = "user")
            User user,
            @PathVariable(value = "type")
            DepositorDocument.Type type,
            Model model
    ){
        List<DepositorDocument> depositorDocuments = depositorService.depositorDocumentsByTypeAndDeposit(user.getDeposit(), type);

        model.addAttribute("documents", depositorDocuments);
        model.addAttribute("document_type", type);

        return "administrator/depositors/documents_type_list";
    }

    @RequestMapping(value = "{user}/documents/{type}/loaddocument", method = RequestMethod.POST)
    public String documentsByTypeLoadDocument(
            @PathVariable(value = "user")
            User user,
            @PathVariable(value = "type")
            DepositorDocument.Type type,
            @RequestParam(value = "document_file")
            MultipartFile multipartFile,
            Model model
    ){
        try {
            depositorService.addDepositorDocument(user.getDeposit(), multipartFile, type);
        } catch (UploadFileException e) {
            e.printStackTrace();
        }

        return "redirect:/administrator/depositor/" + user.getId() + "/documents/" + type.getAlias();
    }

    @RequestMapping(value = "{user}/documents/{type}/validate/{document}")
    public String documentsByTypeValidate(
            @PathVariable(value = "user")
            User user,
            @PathVariable(value = "type")
            DepositorDocument.Type type,
            @PathVariable(value = "document")
            DepositorDocument depositorDocument
    ){
        depositorService.validateDepositorDocument(depositorDocument);

        return "redirect:/administrator/depositor/" + user.getId() + "/documents/" + type.getAlias();
    }

    @RequestMapping(value = "{user}/documents/{type}/delete/{document}", method = RequestMethod.DELETE)
    public String documentsByTypeDelete(
            @PathVariable(value = "user")
            User user,
            @PathVariable(value = "type")
            DepositorDocument.Type type,
            @PathVariable(value = "document")
            DepositorDocument depositorDocument
    ){
        depositorService.deleteDepositorDocument(depositorDocument);

        return "redirect:/administrator/depositor/" + user.getId() + "/documents/" + type.getAlias();
    }

    @RequestMapping(value = "{user}/documents/{type}/download/{uploadedFile}")
    public void getUploadedFile(
            @PathVariable(value = "user")
            User user,
            @PathVariable(value = "type")
            DepositorDocument.Type type,
            @PathVariable(value = "uploadedFile")
            UploadedFile uploadedFile,
            HttpServletResponse response,
            HttpServletRequest request
    ) throws NotFoundException {
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

}
