package com.mfodepositorsacc.dmodel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by berz on 04.05.15.
 */
@Entity(name = "Deposit")
@Table(name = "deposit")
@Access(value = AccessType.FIELD)
public class Deposit extends DModelEntityFiscalable {

    public Deposit(){}

    public Deposit(BigDecimal sum, Integer length, DepositorTypeSettings.DepositorFormType depositorFormType){
        this.setSum(sum);
        this.setLength(length);
        this.setDepositorFormType(depositorFormType);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "deposit_id_generator")
    @SequenceGenerator(name = "deposit_id_generator", sequenceName = "deposit_id_seq")
    @NotNull
    @Column(updatable = false, insertable = false, columnDefinition = "bigint")
    protected Long id;

    protected BigDecimal sum;

    protected Integer length;

    protected BigDecimal percent;

    @Enumerated(EnumType.STRING)
    @Column(name = "depositor_form_type")
    protected DepositorTypeSettings.DepositorFormType depositorFormType;

    @Column(name = "next_capitalization", columnDefinition = "date")
    protected Date nextCapitalization;

    protected boolean activated;

    @JoinColumn(name = "depositor_id")
    @OneToOne
    protected Depositor depositor;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Deposit) &&
                this.getId().equals(((Deposit) obj).getId());
    }

    @Override
    public String toString() {
        return "{deposit#".concat(this.getId().toString()).concat("}");
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public BigDecimal getPercent() {
        return percent;
    }

    public void setPercent(BigDecimal percent) {
        this.percent = percent;
    }

    public Date getNextCapitalization() {
        return nextCapitalization;
    }

    public void setNextCapitalization(Date nextCapitalization) {
        this.nextCapitalization = nextCapitalization;
    }

    public DepositorTypeSettings.DepositorFormType getDepositorFormType() {
        return depositorFormType;
    }

    public void setDepositorFormType(DepositorTypeSettings.DepositorFormType depositorFormType) {
        this.depositorFormType = depositorFormType;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public Depositor getDepositor() {
        return depositor;
    }

    public void setDepositor(Depositor depositor) {
        this.depositor = depositor;
    }
}
