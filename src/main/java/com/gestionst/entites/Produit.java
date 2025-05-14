package com.gestionst.entites;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Produit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProduit;

    private String nom;
    private double prix;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idCategorie")
    private Categorie categorie;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idStock")
    private Stock stock;
}
