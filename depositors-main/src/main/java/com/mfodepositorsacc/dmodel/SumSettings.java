package com.mfodepositorsacc.dmodel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by berz on 26.04.15.
 */
@Entity(name = "SumSettings")
@Table(
        name = "sum_settings",
        uniqueConstraints = @UniqueConstraint(columnNames = {"sum_from", "depositor_type_settings_id"})
)
@Access(AccessType.FIELD)
public class SumSettings extends DModelEntityFiscalable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "sum_settings_id_generator")
    @SequenceGenerator(name = "sum_settings_id_generator", sequenceName = "sum_settings_id_seq")
    @NotNull
    @Column(updatable = false, insertable = false, columnDefinition = "bigint")
    private Long id;

    @NotNull
    @Column(name = "sum_from")
    private BigDecimal sumFrom;

    @NotNull
    @Column(name = "min_length")
    private Integer minLength;

    @NotNull
    @Column(name = "max_length")
    private Integer maxLength;

    @JoinColumn(name = "depositor_type_settings_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private DepositorTypeSettings depositorTypeSettings;

    @OneToMany(mappedBy = "sumSettings")
    @OrderBy("lengthFrom ASC")
    private List<Percents> percentsList;

    public SumSettings(){}


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
        return obj instanceof SumSettings && this.getId().equals(((SumSettings) obj).getId());
    }

    @Override
    public String toString() {
        return "{settings}";
    }

    public BigDecimal getSumFrom() {
        return sumFrom;
    }

    public void setSumFrom(BigDecimal sumFrom) {
        this.sumFrom = sumFrom;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public DepositorTypeSettings getDepositorTypeSettings() {
        return depositorTypeSettings;
    }

    public void setDepositorTypeSettings(DepositorTypeSettings depositorTypeSettings) {
        this.depositorTypeSettings = depositorTypeSettings;
    }

    public List<Percents> getPercentsList() {
        return percentsList;
    }

    public void setPercentsList(List<Percents> percentsList) {
        this.percentsList = percentsList;
    }
}
