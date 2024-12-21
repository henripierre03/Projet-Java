package com.ism.data.entities;

import java.time.LocalDateTime;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class Paiement extends AbstractEntity {
    private Double montantPaye;

    // Nav
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
