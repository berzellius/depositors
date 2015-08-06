package com.mfodepositorsacc.dmodel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by berz on 04.06.15.
 */
@Entity(name = "Notification")
@Table(
        name = "notifications"
)
@Access(AccessType.FIELD)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "notification_type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value = "Notification")
public abstract class Notification extends DModelEntityFiscalable {

    public Notification() {
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public static enum Status{
        WAITING, DONE, CANCELLED
    }

    public static enum Template{
        RESTORE_PASSWORD, PROFILE_ACTIVATION
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "notifications_id_generator")
    @SequenceGenerator(name = "notifications_id_generator", sequenceName = "notifications_id_seq")
    @NotNull
    @Column(updatable = false, insertable = false, columnDefinition = "bigint")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Status status;


    @Enumerated(EnumType.STRING)
    private Template template;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

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
        return obj instanceof Notification &&
                this.getId().equals(((Notification) obj).getId());
    }

    @Override
    public String toString() {
        return "notification#".concat(this.getId().toString());
    }
}
