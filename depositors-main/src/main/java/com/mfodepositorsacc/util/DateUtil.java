package com.mfodepositorsacc.util;

import org.springframework.stereotype.Service;

import java.util.Calendar;

/**
 * Created by berz on 01.05.15.
 */
@Service
public interface DateUtil {

    int getDaysBetweenDays(Calendar cal1, Calendar cal2);

}
