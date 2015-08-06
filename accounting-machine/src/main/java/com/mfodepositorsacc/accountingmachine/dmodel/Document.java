package com.mfodepositorsacc.accountingmachine.dmodel;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by berz on 15.01.15.
 */
@Entity(name = "Document")
@Table(name = "money_document")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "key_type", discriminatorType = DiscriminatorType.STRING)
@Access(AccessType.FIELD)
public abstract class Document extends DModelEntityFiscalable {


    protected Document() {
    }


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "doc_id_generator")
    @SequenceGenerator(name = "doc_id_generator", sequenceName = "money_document_id_seq")
    @NotNull
    @Column(updatable = false, insertable = false, columnDefinition = "bigint")
    protected long id;


    @Column(unique = true)
    @NotNull
    protected String num;

    @Column
    protected String memo;


    @NotNull
    @Column(name = "dt_acc", columnDefinition = "date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    protected Date dtAcc;






    @OneToMany(fetch = FetchType.LAZY, mappedBy = "document")
    protected List<AccountTransaction> accountTransactions = new ArrayList<AccountTransaction>();


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "stornDocument")
    protected List<AccountTransaction> stornedAccountTransactions = new ArrayList<AccountTransaction>();


    /**
     * Атрибут Сумма в документе общего назначения.
     * Контекст использования зависит от бизнес-процесса.
     * Как правило, если по документу одна проводка, то ее сумма берется из этого поля.
     */
    @NotNull
    @Column(name = "sum")
    protected BigDecimal sum;


    @Override
    public boolean equals(Object obj) {
        return obj instanceof Document && getId() == ((Document) obj).getId();
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNum(String num) {
        this.num = num;
    }


    public String getNum() {
        return num;
    }


    public List<AccountTransaction> getAccountTransactions() {
        return accountTransactions;
    }

    public void setAccountTransactions(List<AccountTransaction> accountTransactions) {
        this.accountTransactions = accountTransactions;
    }


    public List<AccountTransaction> getStornedAccountTransactions() {
        return stornedAccountTransactions;
    }

    public void setStornedAccountTransactions(List<AccountTransaction> stornedAccountTransactions) {
        this.stornedAccountTransactions = stornedAccountTransactions;
    }

     /* @Column(name="key_state",insertable = false, updatable = false)
    protected Integer state;*/

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }


    public Date getDtAcc() {
        return dtAcc;
    }

    public void setDtAcc(Date dtAcc) {
        this.dtAcc = dtAcc;
    }




}

