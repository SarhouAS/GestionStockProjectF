package com.gestionst.services;

import com.gestionst.entites.Utilisateur;
import com.gestionst.repositoriesJPA.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public Utilisateur ajouterUtilisateur(Utilisateur utilisateur) {
        System.out.println("Sauvegarde de l'utilisateur : " + utilisateur.getEmail()); // Log pour déboguer
        return utilisateurRepository.save(utilisateur);
    }

    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    public Optional<Utilisateur> getUtilisateurById(Long id) {
        return utilisateurRepository.findById(id);
    }

    public Utilisateur modifierUtilisateur(Long id, Utilisateur utilisateurDetails) {
        return utilisateurRepository.findById(id).map(utilisateur -> {
            utilisateur.setNom(utilisateurDetails.getNom());
            utilisateur.setNumero(utilisateurDetails.getNumero());
            utilisateur.setEmail(utilisateurDetails.getEmail());
            utilisateur.setMdp(utilisateurDetails.getMdp());
            return utilisateurRepository.save(utilisateur);
        }).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    public void supprimerUtilisateur(Long id) {
        utilisateurRepository.deleteById(id);
    }
    
    public Optional<Utilisateur> findByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }
}
