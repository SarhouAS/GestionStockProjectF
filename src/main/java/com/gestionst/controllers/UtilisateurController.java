package com.gestionst.controllers;

import com.gestionst.entites.Role;
import com.gestionst.entites.Utilisateur;
import com.gestionst.services.RoleService;
import com.gestionst.services.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/utilisateurs") 
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private RoleService roleService;

    @PostMapping
    public Utilisateur ajouterUtilisateur(@RequestBody Utilisateur utilisateur) {
        // Si aucun rôle n'est spécifié ou si le rôle spécifié n'a pas d'ID valide
        if (utilisateur.getRole() == null || 
            (utilisateur.getRole().getIdRole() == null && 
             (utilisateur.getRole().getLibelle() == null || utilisateur.getRole().getLibelle().isEmpty()))) {
            
            // Utiliser la nouvelle méthode pour obtenir ou créer le rôle USER
            Role userRole = roleService.getOrCreateUserRole();
            utilisateur.setRole(userRole);
        } 
        // Si un rôle avec ID est spécifié, vérifier s'il existe
        else if (utilisateur.getRole().getIdRole() != null) {
            Optional<Role> existingRole = roleService.getRoleById(utilisateur.getRole().getIdRole());
            if (existingRole.isPresent()) {
                utilisateur.setRole(existingRole.get());
            } else {
                // Si le rôle avec cet ID n'existe pas, utiliser le rôle USER par défaut
                Role userRole = roleService.getOrCreateUserRole();
                utilisateur.setRole(userRole);
            }
        }
        // Si un rôle avec libellé mais sans ID est spécifié
        else if (utilisateur.getRole().getLibelle() != null && !utilisateur.getRole().getLibelle().isEmpty()) {
            // Chercher si ce rôle existe déjà
            Optional<Role> existingRole = roleService.getRoleByLibelle(utilisateur.getRole().getLibelle());
            if (existingRole.isPresent()) {
                utilisateur.setRole(existingRole.get());
            }
            // Si le rôle n'existe pas, il sera créé automatiquement grâce à CascadeType.ALL
        }
        
        return utilisateurService.ajouterUtilisateur(utilisateur);
    }

    @GetMapping
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurService.getAllUtilisateurs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> getUtilisateurById(@PathVariable Long id) {
        Optional<Utilisateur> utilisateur = utilisateurService.getUtilisateurById(id);
        return utilisateur.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Utilisateur> modifierUtilisateur(@PathVariable Long id, @RequestBody Utilisateur utilisateur) {
        try {
            Utilisateur updatedUtilisateur = utilisateurService.modifierUtilisateur(id, utilisateur);
            return ResponseEntity.ok(updatedUtilisateur);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerUtilisateur(@PathVariable Long id) {
        utilisateurService.supprimerUtilisateur(id);
        return ResponseEntity.noContent().build();
    }
}
