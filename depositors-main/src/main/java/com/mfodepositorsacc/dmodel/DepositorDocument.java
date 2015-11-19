package com.mfodepositorsacc.dmodel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by berz on 17.11.2015.
 */
@Entity(name = "DepositorDocument")
@Table(name = "depositor_document")
@Access(value = AccessType.FIELD)
public class DepositorDocument extends DModelEntityFiscalable {

    public DepositorDocument(){};

    public Deposit getDeposit() {
        return deposit;
    }

    public void setDeposit(Deposit deposit) {
        this.deposit = deposit;
    }

    public static enum Type{
        INSURANCE("insurance");

        private String alias;

        Type(String alias){
            this.setAlias(alias);
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public static Type getByAlias(String alias){
            for(Type t : Type.values()){
                if(t.getAlias().equals(alias)){
                    return t;
                }
            }

            return null;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "depositor_id_generator")
    @SequenceGenerator(name = "depositor_id_generator", sequenceName = "depositor_id_seq")
    @NotNull
    @Column(updatable = false, insertable = false, columnDefinition = "bigint")
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private Type type;

    private Boolean deleted;

    private Boolean validated;

    @OneToOne
    @JoinColumn(name = "uploaded_file_id")
    private UploadedFile uploadedFile;

    @OneToOne
    @JoinColumn(name = "deposit_id")
    private Deposit deposit;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Boolean getValidated() {
        return validated;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DepositorDocument && this.getId().equals(((DepositorDocument) obj).getId());
    }

    @Override
    public String toString() {
        return "DepositorDocument#".concat(this.getId().toString());
    }
}
