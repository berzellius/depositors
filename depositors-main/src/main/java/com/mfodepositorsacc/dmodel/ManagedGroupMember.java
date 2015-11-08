package com.mfodepositorsacc.dmodel;

import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * Created by berz on 25.10.2015.
 */
@Entity(name = "ManagedGroupMember")
@Table(name = "managed_groups_members")
@Access(AccessType.FIELD)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "mgm_type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value = "ManagedGroupMember")
public abstract class ManagedGroupMember extends DModelEntityNamed {

    public ManagedGroupMember(){}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "managed_group_members_id_generator")
    @SequenceGenerator(name = "managed_group_members_id_generator", sequenceName = "managed_group_members_id_seq")
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

    @ManyToMany(mappedBy = "managedGroupMembers")
    private Set<ManagedGroup> managedGroups;


    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ManagedGroupMember) && ((ManagedGroupMember) obj).getId().equals(this.getId());
    }

    @Override
    public String toString() {
        return "ManagedGroupMember#".concat(this.getId().toString());
    }

    public Set<ManagedGroup> getManagedGroups() {
        return managedGroups;
    }

    public void setManagedGroups(Set<ManagedGroup> managedGroups) {
        this.managedGroups = managedGroups;
    }
}
