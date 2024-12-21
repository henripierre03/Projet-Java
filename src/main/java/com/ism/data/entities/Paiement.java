package com.ism.data.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "paiements")
public class Paiement extends AbstractEntity {

    @Column(name = "montant_paye", nullable = false)
    private Double montantPaye;

    // Relation avec Dette
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dette_id", nullable = false)
    private Dette dette;

    public Paiement(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, Double montantPaye, Dette dette) {
        super(id, createdAt, updatedAt);
        this.montantPaye = montantPaye;
        this.dette = dette;
    }

    public Paiement() {
        super.createdAt = LocalDateTime.now();
        super.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Paiement [idPaiement=" + super.getId() + ", datePaiement=" + super.getCreatedAt() + ", montantPaye="
                + montantPaye
                + ", dette=" + dette + ", updateAt=" + super.getUpdatedAt() + "]";
    }
}
