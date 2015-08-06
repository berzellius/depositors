package com.mfodepositorsacc.dto;

/**
 * Created by berz on 08.07.15.
 */
public class AccountSaldoGraphDescriptionElement {

    /*
    *
    * Unix timestamp - по горизонтальной оси
     */
    private Long timestamp;

    private String description;

    public AccountSaldoGraphDescriptionElement(long time, String desc) {
        this.setTimestamp(time);
        this.setDescription(desc);
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
