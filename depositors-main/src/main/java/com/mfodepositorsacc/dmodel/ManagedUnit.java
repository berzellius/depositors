package com.mfodepositorsacc.dmodel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * Created by berz on 25.10.2015.
 */
@Entity(name = "ManagedUnit")
@Table(
        name = "managed_units"
)
@Access(AccessType.FIELD)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "mu_type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value = "ManagedUnit")
public abstract class ManagedUnit extends DModelEntityNamed {

    public ManagedUnit(){}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "managed_units_id_generator")
    @SequenceGenerator(name = "managed_units_id_generator", sequenceName = "managed_units_id_seq")
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

    @ManyToMany
    @JoinTable(
            name = "users_to_managed_units",
            joinColumns = @JoinColumn(name = "managed_unit_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users;

    @Override
    public boolean equals(Object obj){
        return (obj instanceof ManagedUnit) && ((ManagedUnit) obj).getId().equals(this.getId());
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
