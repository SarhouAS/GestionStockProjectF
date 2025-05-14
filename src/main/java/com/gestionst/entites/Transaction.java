package com.gestionst.entites;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTransaction;

    private String type;
    private Date date;
    private int quantite;

    @ManyToOne
    @JoinColumn(name = "idUser")
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "idStock")
    private Stock stock;
}
