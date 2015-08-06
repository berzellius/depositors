package com.mfodepositorsacc.service;

import com.mfodepositorsacc.dmodel.Notification;
import com.mfodepositorsacc.exceptions.NotificationSendException;
import com.mfodepositorsacc.exceptions.WrongInputDataException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 * Created by berz on 04.06.15.
 */
public interface EmailMessageSender extends MessageSender {

    void setMailSender(MailSender mailSender);

    void setTemplateMessage(SimpleMailMessage templateMessage);

    void process(Notification notification) throws WrongInputDataException, NotificationSendException;

    void sendEmail(Notification notification) throws NotificationSendException;
}
