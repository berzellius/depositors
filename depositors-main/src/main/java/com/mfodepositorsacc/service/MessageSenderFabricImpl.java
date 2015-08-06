package com.mfodepositorsacc.service;

import com.mfodepositorsacc.dmodel.EmailNotification;
import com.mfodepositorsacc.dmodel.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by berz on 04.06.15.
 */
@Component
public class MessageSenderFabricImpl implements MessageSenderFabric {

    @Autowired
    EmailMessageSender emailMessageSender;

    @Override
    public MessageSender getMessageSender(Notification notification) {
        if(notification instanceof EmailNotification)
            return emailMessageSender;

        return null;
    }

}
