package com.mfodepositorsacc.dto;

import java.math.BigDecimal;

/**
 * Created by berz on 15.05.15.
 */
public class DepositMarketingDTO {

    private BigDecimal inYear;

    private BigDecimal inMonth;

    private BigDecimal inWeek;

    private BigDecimal inDay;

    private BigDecimal percentsInYear;

    private BigDecimal percentsInMonth;

    private BigDecimal percentsInWeek;

    private BigDecimal percentsInDay;

    public BigDecimal getInYear() {
        return inYear;
    }

    public void setInYear(BigDecimal inYear) {
        this.inYear = inYear;
    }

    public BigDecimal getInMonth() {
        return inMonth;
    }

    public void setInMonth(BigDecimal inMonth) {
        this.inMonth = inMonth;
    }

    public BigDecimal getInWeek() {
        return inWeek;
    }

    public void setInWeek(BigDecimal inWeek) {
        this.inWeek = inWeek;
    }

    public BigDecimal getInDay() {
        return inDay;
    }

    public void setInDay(BigDecimal inDay) {
        this.inDay = inDay;
    }

    public BigDecimal getPercentsInYear() {
        return percentsInYear;
    }

    public void setPercentsInYear(BigDecimal percentsInYear) {
        this.percentsInYear = percentsInYear;
    }

    public BigDecimal getPercentsInMonth() {
        return percentsInMonth;
    }

    public void setPercentsInMonth(BigDecimal percentsInMonth) {
        this.percentsInMonth = percentsInMonth;
    }

    public BigDecimal getPercentsInWeek() {
        return percentsInWeek;
    }

    public void setPercentsInWeek(BigDecimal percentsInWeek) {
        this.percentsInWeek = percentsInWeek;
    }

    public BigDecimal getPercentsInDay() {
        return percentsInDay;
    }

    public void setPercentsInDay(BigDecimal percentsInDay) {
        this.percentsInDay = percentsInDay;
    }
}
