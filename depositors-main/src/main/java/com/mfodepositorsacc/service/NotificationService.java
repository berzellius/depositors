package com.mfodepositorsacc.service;

import com.mfodepositorsacc.dmodel.Notification;
import com.mfodepositorsacc.exceptions.NotificationSendException;
import com.mfodepositorsacc.exceptions.WrongInputDataException;
import org.springframework.stereotype.Service;

/**
 * Created by berz on 04.06.15.
 */
@Service
public interface NotificationService {

    public void send(Notification notification) throws WrongInputDataException, NotificationSendException;
}
