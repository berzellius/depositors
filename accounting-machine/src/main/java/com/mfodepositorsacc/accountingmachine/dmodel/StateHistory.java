package com.mfodepositorsacc.accountingmachine.dmodel;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by berz on 15.01.15.
 */
@Entity(name = "StateHistory")
@Table(name = "money_state_history")
@Access(AccessType.FIELD)
public class StateHistory extends DModelEntityFiscalable{


    public StateHistory(String entityType, Long entityId, String fromState, String toState) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.fromState = fromState;
        this.toState = toState;
        this.dtmUpdate = new Date();
        this.dtmCreate = new Date();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "money_state_history_id_generator")
    @SequenceGenerator(name = "money_state_history_id_generator", sequenceName = "money_state_history_id_seq")
    @NotNull
    @Column(updatable = false, insertable = false, columnDefinition = "bigint")
    protected Long id;


    @Column(name = "id_entity")
    protected Long entityId;

    @Column(name = "key_type_entity")
    @NotNull
    protected String entityType;

    @NotNull
    @Column(name = "from_state")
    protected String fromState;

    @NotNull
    @Column(name = "to_state")
    protected String toState;


    public String getToState() {
        return toState;
    }

    public void setToState(String toState) {
        this.toState = toState;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StateHistory)) return false;

        StateHistory that = (StateHistory) o;

        if (getId() != that.getId()) return false;

        return true;
    }

    @Override
    public String toString() {
        return "{state history element}";
    }

    @Override
    public int hashCode() {
        int result = fromState.hashCode();
        result = 31 * result + toState.hashCode();
        return result;
    }

    public StateHistory() {

    }

    public void setFromState(String fromState) {
        this.fromState = fromState;
    }

    public String getFromState() {
        return fromState;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
}
