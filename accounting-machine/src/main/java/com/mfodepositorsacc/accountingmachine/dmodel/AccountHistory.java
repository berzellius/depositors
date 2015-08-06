package com.mfodepositorsacc.accountingmachine.dmodel;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by berz on 15.01.15.
 */
@Entity(name = "AccountHistory")
@Table(name = "accounts_history")
@Access(value = AccessType.FIELD)
public class AccountHistory extends DModelEntityFiscalable {
    public AccountHistory() {
    }

    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setFields(Account a, AccountTransaction tr, Date d) {
        if (a.getSaldo() != null) setSaldo(a.getSaldo());
        else throw new NullPointerException("Null saldo value given while creating AccountHistory object!");


        setDtmUpdate(d);


        setAccount(a);

        if (a.getCode() != null) setCode(a.getCode());
        else throw new NullPointerException("Null code value given while creating AccountHistory object!");

        if (a.getBehavior() != null) setBehavior(a.getBehavior());
        else throw new NullPointerException("Null behavior value given while creating AccountHistory object!");

        if (a.getIdDeposit() != null) setIdDeposit(a.getIdDeposit());
        else
            throw new NullPointerException("Null id_account analytic value given while creating AccountHistory object!");



        setMemo(a.getMemo());

        if (a.isRedSaldo() != null) {
            setRedSaldo(a.isRedSaldo());

            setRedSaldo(false);
        }


        if (a.getSchema() != null) setSchema(a.getSchema());
        else throw new NullPointerException("Null schema value given while creating AccountHistory object!");

        if (tr != null) {
            setAccountTransaction(tr);
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "acc_hist_id_generator")
    @SequenceGenerator(name = "acc_hist_id_generator", sequenceName = "money_account_history_id_seq")
    @NotNull
    @Column(updatable = false, insertable = false, columnDefinition = "bigint")
    protected Long id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "id_acc")
    protected Account account;

    @NotNull
    @Column(name = "saldo")
    protected BigDecimal saldo;

    @NotNull
    @Column(name = "red_saldo")
    protected boolean redSaldo;

    @Length(max = 1024)
    protected String memo;

    @NotNull
    @Column(name = "id_deposit")
    private Integer idDeposit;

    @NotNull
    @Length(max = 1024)
    protected String schema;

    @Length(max = 255)
    @NotNull
    protected String code;

    @NotNull
    protected Account.Behavior behavior;

    @OneToOne
    @JoinColumn(name = "id_acc_transaction")
    protected AccountTransaction accountTransaction;

    public AccountTransaction getAccountTransaction() {
        return accountTransaction;
    }

    public void setAccountTransaction(AccountTransaction accountTransaction) {
        this.accountTransaction = accountTransaction;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }



    public boolean isRedSaldo() {
        return redSaldo;
    }

    public void setRedSaldo(boolean redSaldo) {
        this.redSaldo = redSaldo;
    }




    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Account.Behavior getBehavior() {
        return behavior;
    }

    public void setBehavior(Account.Behavior behavior) {
        this.behavior = behavior;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AccountHistory && getId() == ((AccountHistory) obj).getId();
    }

    @Override
    public String toString() {
        return "{accountHistory}";
    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + getAccount().hashCode();
        result = 31 * result + (getIdDeposit() != null ? getIdDeposit().hashCode() : 0);
        return result;
    }

    public Integer getIdDeposit() {
        return idDeposit;
    }

    public void setIdDeposit(Integer idDeposit) {
        this.idDeposit = idDeposit;
    }
}
