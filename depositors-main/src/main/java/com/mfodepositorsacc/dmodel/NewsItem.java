package com.mfodepositorsacc.dmodel;

import org.aspectj.apache.bcel.generic.NEW;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * Created by berz on 25.10.2015.
 */
@Entity(name = "NewsItem")
@Table(name = "news_items")
@Access(AccessType.FIELD)
public class NewsItem extends DModelEntityNamed {

    public NewsItem(){}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "news_items_id_generator")
    @SequenceGenerator(name = "news_items_id_generator", sequenceName = "news_items_id_seq")
    @NotNull
    @Column(updatable = false, insertable = false, columnDefinition = "bigint")
    private Long id;

    private Boolean published;

    private Boolean deleted;

    @ManyToMany
    @JoinTable(
            name = "news_to_managed_units",
            joinColumns = @JoinColumn(name = "news_item_id"),
            inverseJoinColumns = @JoinColumn(name = "managed_unit_id")
    )
    private Set<ManagedUnit> managedUnits;

    /*
    * Обновлять список пользователей при появлении в системе новых пользователей, подходящих под список рассылки.
     */
    @Column(name = "users_updatable")
    private Boolean usersUpdatable;

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
        return (obj instanceof NewsItem) && ((NewsItem) obj).getId().equals(this.getId());
    }

    @Override
    public String toString() {
        return "NewsItem#".concat(this.getId().toString());
    }


    public Set<ManagedUnit> getManagedUnits() {
        return managedUnits;
    }

    public void setManagedUnits(Set<ManagedUnit> managedUnits) {
        this.managedUnits = managedUnits;
    }

    public Boolean getUsersUpdatable() {
        return usersUpdatable;
    }

    public void setUsersUpdatable(Boolean usersUpdatable) {
        this.usersUpdatable = usersUpdatable;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
