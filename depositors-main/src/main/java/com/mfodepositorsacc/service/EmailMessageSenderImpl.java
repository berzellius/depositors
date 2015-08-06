package com.mfodepositorsacc.service;

import com.mfodepositorsacc.dmodel.EmailNotification;
import com.mfodepositorsacc.dmodel.Notification;
import com.mfodepositorsacc.exceptions.NotificationSendException;
import com.mfodepositorsacc.exceptions.WrongInputDataException;
import com.mfodepositorsacc.preparator.MimeMessagePreparatorImpl;
import com.mfodepositorsacc.repository.NotificationRepository;
import com.mfodepositorsacc.util.LinkBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by berz on 04.06.15.
 */
@Service
@Transactional
public class EmailMessageSenderImpl implements EmailMessageSender {

    @Autowired
    LinkBuilder linkBuilder;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    VelocityEngineFactoryBean velocityEngineFactoryBean;

    private MailSender mailSender;
    private SimpleMailMessage templateMessage;


    @Override
    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void setTemplateMessage(SimpleMailMessage templateMessage) {
        this.templateMessage = templateMessage;
    }

    @Override
    public void send(Notification notification) throws WrongInputDataException, NotificationSendException {
        if (!(notification instanceof EmailNotification)) {
            throw new IllegalArgumentException("wrong notification type!");
        }

        process(notification);
        notification.setStatus(Notification.Status.DONE);

        notificationRepository.save(notification);
    }

    @Override
    public void process(Notification notification) throws WrongInputDataException, MailException, NotificationSendException {
        if (notification.getUser() == null) {
            throw new WrongInputDataException("null user field!", WrongInputDataException.Reason.USERNAME_FIELD);
        }

        if (notification.getUser().getDeposit() == null) {
            throw new WrongInputDataException("null deposit!", WrongInputDataException.Reason.DEPOSIT_FIELD);
        }

        if (notification.getUser().getDeposit().getDepositor() == null) {
            throw new WrongInputDataException("null depositor", WrongInputDataException.Reason.DEPOSITOR_FIELD);
        }

        if (notification.getUser().getDeposit().getDepositor().getEmail() == null) {
            throw new WrongInputDataException("null email", WrongInputDataException.Reason.EMAIL_FIELD);
        }

        sendEmail(notification);

    }

    @Override
    public void sendEmail(final Notification notification) throws NotificationSendException {

        String email = notification.getUser().getDeposit().getDepositor().getEmail();
        HashMap<String, Object> params = this.emailTemplateParameters(notification);
        MimeMessagePreparator preparator = new MimeMessagePreparatorImpl(
                this.templateMessage,
                VelocityEngineUtils.mergeTemplateIntoString(
                        velocityEngineFactoryBean.getObject(),
                        (String) params.get("template"),
                        "UTF-8",
                        (HashMap<String, Object>) params.get("tmplParams")
                ), (String)params.get("subject"), email);

        try {
            if (this.mailSender instanceof JavaMailSenderImpl) {
                JavaMailSenderImpl javaMailSender = (JavaMailSenderImpl) this.mailSender;
                javaMailSender.send(preparator);
            } else {
                throw new NotificationSendException("can work only with JavaMailSenderImpl implementation of org.springframework.mail.MailSender!");
            }
        } catch (MailException e) {
            //e.printStackTrace();
            throw new NotificationSendException("cant send email to ".concat(email));
        }

    }

    private HashMap<String, Object> emailTemplateParameters(Notification notification) throws NotificationSendException {
        HashMap<String, Object> params = new LinkedHashMap<String, Object>();
        HashMap<String, Object> tmplParams = null;

        switch (notification.getTemplate()) {
            case PROFILE_ACTIVATION:
                tmplParams = new LinkedHashMap<String, Object>();
                tmplParams.put("activationLink", linkBuilder.getProfileActivation(notification.getUser()));

                params.put("template", "/registration_confirm.vm");
                params.put("subject", "Подтверждение регистрации на yazaimy.ru");
                params.put("tmplParams", tmplParams);
                break;
            case RESTORE_PASSWORD:
                tmplParams = new LinkedHashMap<String, Object>();
                tmplParams.put("activationLink", linkBuilder.getProfilePasswordRestore(notification.getUser()));

                params.put("template", "/restore_password.vm");
                params.put("subject", "Восстановление пароля на yazaimy.ru");
                params.put("tmplParams", tmplParams);
                break;
            default:
                throw new NotificationSendException("No template data for this notification type");
        }

        return params;
    }
}
