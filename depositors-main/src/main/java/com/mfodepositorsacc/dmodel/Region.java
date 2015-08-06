package com.mfodepositorsacc.dmodel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by berz on 07.05.15.
 */
@Entity(name = "Region")
@Table(
        name = "region"
)
@Access(AccessType.FIELD)
public class Region extends DModelEntityNamed {

    public Region(){}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "region_id_generator")
    @SequenceGenerator(name = "region_id_generator", sequenceName = "region_id_seq")
    @NotNull
    @Column(updatable = false, insertable = false, columnDefinition = "bigint")
    private Long id;

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
        return obj instanceof Region && this.getId().equals(((Region) obj).getId());
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
