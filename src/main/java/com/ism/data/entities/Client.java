package com.ism.data.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false, exclude = { "demandeDettes", "dettes", "user" })
@Entity
@Table(name = "clients")
public class Client extends AbstractEntity {

    @Column(unique = true, nullable = false, length = 100)
    private String surname;

    @Column(unique = true, nullable = false, length = 20)
    private String tel;

    @Column(length = 255)
    private String address;

    @Column(name = "cumul_montant_du")
    private Double cumulMontantDu;

    @Column(nullable = false)
    private boolean status;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DemandeDette> demandeDettes = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Dette> dettes = new ArrayList<>();

    public Client(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, String surname, String tel, String address,
            Double cumulMontantDu, boolean status, User user) {
        super(id, createdAt, updatedAt);
        this.surname = surname;
        this.tel = tel;
        this.address = address;
        this.cumulMontantDu = cumulMontantDu;
        this.status = status;
        this.user = user;
    }

    public Client() {
        super();
        super.createdAt = LocalDateTime.now();
        super.updatedAt = LocalDateTime.now();
        this.cumulMontantDu = 0.0;
    }

    public void addDemandeDette(DemandeDette demandeDette) {
        demandeDettes.add(demandeDette);
        demandeDette.setClient(this);
    }

    public void addDetteClient(Dette dette) {
        dettes.add(dette);
        dette.setClient(this);
        updateCumulMontantDu();
    }

    // Modification de la méthode getCumulMontantDu pour éviter les accès lazy
    public Double getCumulMontantDu() {
        return this.cumulMontantDu;
    }

    // Méthode séparée pour mettre à jour le cumul
    public void updateCumulMontantDu() {
        double newCumul = 0.0;
        if (dettes != null) {
            for (Dette dette : dettes) {
                newCumul += dette.getMontantRestant();
            }
        }
        this.cumulMontantDu = newCumul;
    }

    @Override
    public String toString() {
        String display = "Surname : " + surname + ", Tel : " + tel + ", Adresse : " + address;
        // return "Client [id=" + super.getId() + ", surname=" + surname + ", tel=" + tel + ", address=" + address +
        //         ", cumulMontantDu=" + cumulMontantDu + ", status=" + status + ", user=" + user +
        //         ", createAt=" + super.getCreatedAt() + ", updateAt=" + super.getUpdatedAt() + "]";
        return display;
    }
}