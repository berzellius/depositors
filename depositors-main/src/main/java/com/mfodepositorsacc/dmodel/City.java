package com.mfodepositorsacc.dmodel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by berz on 07.05.15.
 */
@Entity(name = "City")
@Table(
        name = "city"
)
@Access(AccessType.FIELD)
public class City extends DModelEntityNamed {

    public City(){}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "city_id_generator")
    @SequenceGenerator(name = "city_id_generator", sequenceName = "city_id_seq")
    @NotNull
    @Column(updatable = false, insertable = false, columnDefinition = "bigint")
    private Long id;

    @OneToOne
    @JoinColumn(name = "region_id")
    private Region region;

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
        return obj instanceof City && this.getId().equals(((City) obj).getId());
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }
}
