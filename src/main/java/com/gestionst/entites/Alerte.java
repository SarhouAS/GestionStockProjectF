package com.gestionst.entites;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alerte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAlerte;

    private int seuil;

    @ManyToOne
    @JoinColumn(name = "idUser")
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "idProduit")
    private Produit produit;
}
