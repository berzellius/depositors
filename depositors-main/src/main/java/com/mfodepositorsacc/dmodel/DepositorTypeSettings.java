package com.mfodepositorsacc.dmodel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by berz on 26.04.15.
 */
@Entity(name = "DepositorTypeSettings")
@Table(
        name = "depositor_type_settings",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"depositor_form_type"}
        )
)
@Access(value = AccessType.FIELD)
public class DepositorTypeSettings extends DModelEntityFiscalable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "depositor_type_settings_id_generator")
    @SequenceGenerator(name = "depositor_type_settings_id_generator", sequenceName = "depositor_type_settings_id_seq")
    @NotNull
    @Column(updatable = false, insertable = false, columnDefinition = "bigint")
    private Long id;

    @Column(name = "min_sum")
    private BigDecimal minSum;

    @Column(name = "max_sum")
    private BigDecimal maxSum;

    @Column(name = "depositor_form_type")
    @Enumerated(value = EnumType.STRING)
    private DepositorFormType depositorFormType;

    @OneToMany(mappedBy = "depositorTypeSettings")
    @OrderBy("sumFrom ASC")
    private List<SumSettings> sumSettingsList;


    public DepositorFormType getDepositorFormType() {
        return depositorFormType;
    }

    public void setDepositorFormType(DepositorFormType depositorFormType) {
        this.depositorFormType = depositorFormType;
    }

    public List<SumSettings> getSumSettingsList() {
        return sumSettingsList;
    }

    public void setSumSettingsList(List<SumSettings> sumSettingsList) {
        this.sumSettingsList = sumSettingsList;
    }

    public enum DepositorFormType{
        ORGANIZATION, PERSON
    }

    public DepositorTypeSettings(){}

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
        return obj instanceof DepositorTypeSettings && this.getId().equals(((DepositorTypeSettings) obj).getId());
    }

    @Override
    public String toString() {
        return this.getDepositorFormType().toString().concat(" settings");
    }

    public BigDecimal getMinSum() {
        return minSum;
    }

    public void setMinSum(BigDecimal minSum) {
        this.minSum = minSum;
    }

    public BigDecimal getMaxSum() {
        return maxSum;
    }

    public void setMaxSum(BigDecimal maxSum) {
        this.maxSum = maxSum;
    }
}
