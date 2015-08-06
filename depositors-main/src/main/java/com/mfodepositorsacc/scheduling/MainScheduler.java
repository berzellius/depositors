package com.mfodepositorsacc.scheduling;

import org.springframework.scheduling.annotation.Scheduled;

/**
 * Created by berz on 05.05.15.
 */
public interface MainScheduler {


    void notificationSendProcess() throws InterruptedException;


    @Scheduled(fixedDelay = 20000)
    void depositsCapitalization();
}
