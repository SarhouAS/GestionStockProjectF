package com.gestionst.entites;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUser;

    private String nom;
    private String numero;
    private String email;
    private String mdp;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idRole")
    private Role role;
}
