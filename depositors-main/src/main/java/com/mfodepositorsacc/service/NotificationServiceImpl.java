package com.mfodepositorsacc.service;

import com.mfodepositorsacc.dmodel.Notification;
import com.mfodepositorsacc.exceptions.NotificationSendException;
import com.mfodepositorsacc.exceptions.WrongInputDataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by berz on 04.06.15.
 */
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    MessageSenderFabric messageSenderFabric;


    @Override
    public void send(Notification notification) throws WrongInputDataException, NotificationSendException {
        messageSenderFabric.getMessageSender(notification).send(notification);
    }
}
