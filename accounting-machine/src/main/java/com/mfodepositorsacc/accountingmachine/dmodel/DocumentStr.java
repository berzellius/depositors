package com.mfodepositorsacc.accountingmachine.dmodel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by berz on 15.01.15.
 */
@Entity(name = "DocumentStr")
@Table(name = "money_document_str")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "key_type", discriminatorType = DiscriminatorType.STRING)
@Access(AccessType.FIELD)
public abstract class DocumentStr extends DModelEntityFiscalable {

    protected DocumentStr() {
    }

    public DocumentStr(Document document) {
        this.document = document;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "doc_str_id_generator")
    @SequenceGenerator(name = "doc_str_id_generator", sequenceName = "money_document_str_id_seq")
    @NotNull
    @Column(updatable = false, insertable = false, columnDefinition = "bigint")
    protected Long id;



    @OneToMany(fetch = FetchType.LAZY, mappedBy = "documentStr")
    protected List<AccountTransaction> accountTransactions = new ArrayList<AccountTransaction>();



    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Document that = (Document) o;

        if (getId() != null ? !getId().equals(that.getId())
                : that.getId() != null) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return (getId() != null ? getId().hashCode() : 0);
    }


    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    @NotNull
    @Column(name = "sum")
    protected BigDecimal sum;


    @ManyToOne
    @JoinColumn(name = "id_document", insertable = false, updatable = false)
    protected Document document;


    protected String memo;

    protected String type;

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public List<AccountTransaction> getAccountTransactions() {
        return accountTransactions;
    }

    public void setAccountTransactions(List<AccountTransaction> accountTransactions) {
        this.accountTransactions = accountTransactions;
    }


}
