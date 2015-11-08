package com.mfodepositorsacc.dmodel;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**
 * Created by berz on 25.10.2015.
 */
@Entity
@Table
@DiscriminatorValue("ManagedGroup")
public class ManagedGroup extends ManagedUnit {

    public ManagedGroup(){}

    @ManyToMany
    @JoinTable(
            name = "managed_groups_to_members",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    private Set<ManagedGroupMember> managedGroupMembers;

    @Override
    public String toString() {
        return "ManagedGroup#".concat(this.getId().toString());
    }

    public Set<ManagedGroupMember> getManagedGroupMembers() {
        return managedGroupMembers;
    }

    public void setManagedGroupMembers(Set<ManagedGroupMember> managedGroupMembers) {
        this.managedGroupMembers = managedGroupMembers;
    }
}
