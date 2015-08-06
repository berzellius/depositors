package com.mfodepositorsacc.accountingmachine.dmodel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by berz on 13.01.15.
 */
@Entity(name = "Account")
@Table(name = "accounts")
@Access(value = AccessType.FIELD)
public class Account extends DModelEntityFiscalable {

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

    public boolean isSynthetic() {
        return isSynthetic;
    }

    public void setSynthetic(boolean isSynthetic) {
        this.isSynthetic = isSynthetic;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Behavior getBehavior() {
        return behavior;
    }

    public void setBehavior(Behavior behavior) {
        this.behavior = behavior;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getIdDeposit() {
        return idDeposit;
    }

    public void setIdDeposit(Integer idDeposit) {
        this.idDeposit = idDeposit;
    }

    public Boolean isRedSaldo() {
        return redSaldo;
    }

    public void setRedSaldo(Boolean redSaldo) {
        this.redSaldo = redSaldo;
    }

    public enum Behavior {
        A("Активный"),
        AP("Активно-пассивный"),
        P("Пассивный");

        private String value;

        Behavior(String value) {
            this.value = value;
        }


        @Override
        public String toString() {
            return this.value;
        }
    }

    public Account(){}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "account_id_generator")
    @SequenceGenerator(name = "account_id_generator", sequenceName = "account_id_seq")
    @NotNull
    @Column(updatable = false, insertable = false, columnDefinition = "bigint")
    private Long id;

    @Column(name = "memo")
    protected String memo;

    protected String schema;

    @Column(name = "is_synthetic")
    protected boolean isSynthetic;

    @Column(name = "has_red_saldo")
    protected Boolean redSaldo;

    @Column(name = "saldo", columnDefinition = "numeric(19,0) default 0")
    protected BigDecimal saldo = BigDecimal.ZERO;

    @NotNull
    @Enumerated(EnumType.STRING)
    protected Behavior behavior;

    // Набор аналитик

    @NotNull
    protected String code;

    @NotNull
    @Column(name = "id_deposit", columnDefinition = "int default 0")
    protected Integer idDeposit = 0;

    // Закончился набор аналитик

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + getCode().hashCode();
        result = 31 * result + (getIdDeposit() != null ? getIdDeposit().hashCode() : 0);

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Account && (this.getId().equals(((Account) obj).getId()));
    }

    @Override
    public String toString() {
        return "{account}";
    }
}
