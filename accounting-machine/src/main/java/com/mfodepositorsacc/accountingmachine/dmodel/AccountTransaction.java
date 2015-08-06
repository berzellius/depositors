package com.mfodepositorsacc.accountingmachine.dmodel;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by berz on 14.01.15.
 */
@Entity(name = "AccountTransaction")
@Table(name = "money_transaction")
public class AccountTransaction extends DModelEntityFiscalable {

    @Deprecated
    public AccountTransaction() {
    }

    public AccountTransaction(Account accountCt, Account accountDt, BigDecimal sum, Date dtAcc, TransactionType type) {
        this.accountCt = accountCt;
        this.accountDt = accountDt;
        this.sum = sum;
        this.dtAcc = dtAcc;
        this.type = type;
        this.setState(State.NEW);
    }


    public AccountTransaction(Account accountCt, Account accountDt, BigDecimal sum, Date dtAcc, TransactionType type, Document document) {
        this.accountCt = accountCt;
        this.accountDt = accountDt;
        this.sum = sum;
        this.dtAcc = dtAcc;
        this.type = type;
        this.document = document;
        this.setState(State.NEW);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "trans_id_generator")
    @SequenceGenerator(name = "trans_id_generator", sequenceName = "money_transaction_id_seq")
    @NotNull
    @Column(updatable = false, insertable = false, columnDefinition = "bigint")
    protected Long id;


    @Column(name = "memo")
    protected String memo;

    @ManyToOne
    @JoinColumn(name = "id_account_ct")
    @NotNull
    protected Account accountCt;

    @ManyToOne
    @JoinColumn(name = "id_account_dt")
    @NotNull
    protected Account accountDt;

    @NotNull
    @Column(name = "sum")
    protected BigDecimal sum;


    @NotNull
    @Column(name = "dt_acc", columnDefinition = "date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    protected Date dtAcc;

    public TransactionType getType() {
        return type;
    }


    public enum TransactionType {

        CLIENT_PAID("Оплата денег клиентом за заказ"),
        DEPOSIT_PAY_IN_ACC("Пополнение счета"),
        DEPOSIT_PERCENTS_PAY("Начисление процентов"),
        DEPOSIT_OUTCOME("Перевод или вывод наличных"),
        DEPOSIT_OUTCOME_LOCK_MONEY("Блокировка средств для вывода");


        private String text;


        TransactionType(String text) {
            this.text = text;
        }

    }


    @Column(name = "key_type")
    @Enumerated(EnumType.STRING)
    @NotNull
    protected TransactionType type;


    public enum State {
        NEW, DONE, CANCELED

    }

    @ManyToOne
    @NotNull
    @JoinColumn(name = "id_document")
    protected Document document;


    @ManyToOne
    @JoinColumn(name = "id_document_str")
    protected DocumentStr documentStr;


    /**
     * Документ, сторнировавший проводку
     */
    @ManyToOne
    @JoinColumn(name = "id_storn_document")
    protected Document stornDocument;


    @Column(name = "key_state")
    @Enumerated(EnumType.STRING)
    protected State state = State.NEW;


    @Override
    public boolean equals(Object obj) {
        return obj instanceof AccountTransaction && getId() == ((AccountTransaction) obj).getId();
    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + getAccountCt().hashCode();
        result = 31 * result + getAccountDt().hashCode();
        //result = 31 * result + document.hashCode();
        //result = 31 * result + documentStr.hashCode();
        return result;
    }


    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }


    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setType(TransactionType type) {
        this.type = type;
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

    public Account getAccountCt() {
        return accountCt;
    }

    public void setAccountCt(Account accountCt) {
        this.accountCt = accountCt;
    }

    public Account getAccountDt() {
        return accountDt;
    }

    public void setAccountDt(Account accountDt) {
        this.accountDt = accountDt;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }


    public void setDocumentStr(DocumentStr documentStr) {
        this.documentStr = documentStr;
    }


    public DocumentStr getDocumentStr() {
        return documentStr;
    }

    public Document getStornDocument() {
        return stornDocument;
    }

    public void setStornDocument(Document stornDocument) {
        this.stornDocument = stornDocument;
    }

    public String toString() {


        StringBuilder sb = new StringBuilder();
        sb.append("'").append(this.getType().text).append("'");
        sb.append("[КТ:").append(this.getAccountCt()).append(" -> ");
        sb.append("ДТ:").append(this.getAccountDt()).append("]");
        sb.append(", Сумма:").append(getSum()).append("]");

        return sb.toString();
    }


}
