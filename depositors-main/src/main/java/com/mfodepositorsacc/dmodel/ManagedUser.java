package com.mfodepositorsacc.dmodel;

import javax.persistence.*;

/**
 * Created by berz on 25.10.2015.
 */
@Entity
@Table
@DiscriminatorValue("ManagedUser")
public class ManagedUser extends ManagedUnit {

    public ManagedUser(){}

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public String toString() {
        return "ManagedUser#".concat(this.getId().toString());
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object obj){
        return (obj instanceof ManagedUser) && ((ManagedUser) obj).getId().equals(this.getId());
    }
}
