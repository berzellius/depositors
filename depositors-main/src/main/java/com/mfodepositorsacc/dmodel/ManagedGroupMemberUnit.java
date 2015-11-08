package com.mfodepositorsacc.dmodel;

import javax.persistence.*;

/**
 * Created by berz on 25.10.2015.
 */
@Entity
@Table
@DiscriminatorValue("ManagedGroupMemberUnit")
public class ManagedGroupMemberUnit extends ManagedGroupMember {

    public ManagedGroupMemberUnit(){}

    @OneToOne
    @JoinColumn(name = "managed_unit_id")
    private ManagedUnit managedUnit;

    @Override
    public boolean equals(Object obj){
        return (obj instanceof ManagedGroupMemberUnit) && ((ManagedGroupMemberUnit) obj).getId().equals(this.getId());
    }

    @Override
    public String toString(){
        return "ManagedGroupMemberUnit#".concat(this.getId().toString());
    }

    public ManagedUnit getManagedUnit() {
        return managedUnit;
    }

    public void setManagedUnit(ManagedUnit managedUnit) {
        this.managedUnit = managedUnit;
    }
}
