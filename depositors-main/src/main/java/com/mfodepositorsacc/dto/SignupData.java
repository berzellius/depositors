package com.mfodepositorsacc.dto;

/**
 * Created by berz on 03.05.15.
 */
public class SignupData {

    public String fio;
    public String email;
    public String mobile;

    public SignupData(String fio, String email, String mobile){
        this.fio = fio;
        this.email = email;
        this.mobile = mobile;
    }

    public SignupData(){}

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
