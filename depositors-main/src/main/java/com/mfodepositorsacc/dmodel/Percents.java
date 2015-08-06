package com.mfodepositorsacc.dmodel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by berz on 26.04.15.
 */
@Entity(name = "Percents")
@Table(
        name = "percents",
        uniqueConstraints = @UniqueConstraint(columnNames = {"length_from", "sum_settings_id"})
)
@Access(AccessType.FIELD)
public class Percents extends DModelEntityFiscalable {

    public Percents(){}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "percents_id_generator")
    @SequenceGenerator(name = "percents_id_generator", sequenceName = "percents_id_seq")
    @NotNull
    @Column(updatable = false, insertable = false, columnDefinition = "bigint")
    private Long id;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "length_from")
    @NotNull
    private Integer lengthFrom;

    @NotNull
    private BigDecimal percent;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sum_settings_id")
    private SumSettings sumSettings;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Percents && this.getId().equals(((Percents) obj).getId());
    }

    @Override
    public String toString() {
        return "{percents}";
    }

    public Integer getLengthFrom() {
        return lengthFrom;
    }

    public void setLengthFrom(Integer lengthFrom) {
        this.lengthFrom = lengthFrom;
    }

    public BigDecimal getPercent() {
        return percent;
    }

    public void setPercent(BigDecimal percent) {
        this.percent = percent;
    }

    public SumSettings getSumSettings() {
        return sumSettings;
    }

    public void setSumSettings(SumSettings sumSettings) {
        this.sumSettings = sumSettings;
    }
}
