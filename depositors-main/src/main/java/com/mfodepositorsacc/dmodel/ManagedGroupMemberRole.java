package com.mfodepositorsacc.dmodel;

import javax.persistence.*;

/**
 * Created by berz on 25.10.2015.
 */
@Entity
@Table
@DiscriminatorValue("ManagedGroupMemberRole")
public class ManagedGroupMemberRole extends ManagedGroupMember {

    public ManagedGroupMemberRole(){}

    @OneToOne
    @JoinColumn(name = "user_role_id")
    private UserRole userRole;

    @Override
    public boolean equals(Object obj){
        return (obj instanceof ManagedGroupMemberRole) && ((ManagedGroupMemberRole) obj).getId().equals(this.getId());
    }

    @Override
    public String toString(){
        return "ManagedGroupMemberRole#".concat(this.getId().toString());
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }
}
