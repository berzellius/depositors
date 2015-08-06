package com.mfodepositorsacc.dmodel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by berz on 11.05.15.
 */
@Entity(name = "Depositor")
@Table(name = "depositor")
@Access(value = AccessType.FIELD)
public class Depositor extends DModelEntityFiscalable {

    public Depositor(){}

    public Citizen getCitizen() {
        return citizen;
    }

    public void setCitizen(Citizen citizen) {
        this.citizen = citizen;
    }

    public String getPassportNum() {
        return passportNum;
    }

    public void setPassportNum(String passportNum) {
        this.passportNum = passportNum;
    }


    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getBirthplace() {
        return birthplace;
    }

    public void setBirthplace(String birthplace) {
        this.birthplace = birthplace;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Date getPassportAccepted() {
        return passportAccepted;
    }

    public void setPassportAccepted(Date passportAccepted) {
        this.passportAccepted = passportAccepted;
    }

    public static enum Citizen{
        YES, NO
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "depositor_id_generator")
    @SequenceGenerator(name = "depositor_id_generator", sequenceName = "depositor_id_seq")
    @NotNull
    @Column(updatable = false, insertable = false, columnDefinition = "bigint")
    private Long id;

    private String fio;

    @Column(name = "mobile_phone")
    private String mobilePhone;

    @Column(name = "home_phone")
    private String homePhone;

    @Column(name = "work_phone")
    private String workPhone;

    @Column(name = "work_addt_phone")
    private String addtPhone;

    @Column(name = "fact_index")
    private String factIndex;

    @OneToOne
    @JoinColumn(name = "fact_city_id")
    private City factCity;

    @Column(name = "reg_index")
    private String regIndex;

    @OneToOne
    @JoinColumn(name = "reg_city_id")
    private City regCity;

    private String email;

    @Enumerated(EnumType.STRING)
    private Citizen citizen;

    @Column(name = "passport_num")
    private String passportNum;

    @Column(name = "passport_accepted")
    private Date passportAccepted;

    @Column(name = "department_name")
    private String departmentName;

    @Column(name = "department_code")
    private String departmentCode;

    private Date birthday;

    private String birthplace;

    private String inn;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Depositor && (((Depositor) obj).getId().equals(this.getId()));
    }

    @Override
    public String toString() {
        return null;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }

    public String getAddtPhone() {
        return addtPhone;
    }

    public void setAddtPhone(String addtPhone) {
        this.addtPhone = addtPhone;
    }

    public String getFactIndex() {
        return factIndex;
    }

    public void setFactIndex(String factIndex) {
        this.factIndex = factIndex;
    }

    public City getFactCity() {
        return factCity;
    }

    public void setFactCity(City factCity) {
        this.factCity = factCity;
    }

    public String getRegIndex() {
        return regIndex;
    }

    public void setRegIndex(String regIndex) {
        this.regIndex = regIndex;
    }

    public City getRegCity() {
        return regCity;
    }

    public void setRegCity(City regCity) {
        this.regCity = regCity;
    }

    public String getFormattedMobilePhone(){
        return "8(9906)743-59-64";
    }
}
