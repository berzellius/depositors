package com.mfodepositorsacc.service;

import com.mfodepositorsacc.dmodel.Notification;
import org.springframework.stereotype.Component;

/**
 * Created by berz on 04.06.15.
 */
@Component
public interface MessageSenderFabric {
    MessageSender getMessageSender(Notification notification);
}
