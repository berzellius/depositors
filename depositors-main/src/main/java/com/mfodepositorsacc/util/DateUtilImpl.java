package com.mfodepositorsacc.util;

import org.springframework.stereotype.Service;

import java.util.Calendar;

/**
 * Created by berz on 01.05.15.
 */
@Service
public class DateUtilImpl implements DateUtil {
    @Override
    public int getDaysBetweenDays(Calendar cal1, Calendar cal2) {

        cal1.set(Calendar.HOUR, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);

        cal2.set(Calendar.HOUR, 0);
        cal2.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);


        return (int)Math.floor(Math.abs((cal1.getTimeInMillis() - cal2.getTimeInMillis()))/(long)(24*3600*1000));
    }
}
