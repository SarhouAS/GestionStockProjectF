package com.gestionst.services;

import com.gestionst.entites.Alerte;
import com.gestionst.entites.Produit;
import com.gestionst.entites.Utilisateur;
import com.gestionst.repositoriesJPA.AlerteRepository;
import com.gestionst.repositoriesJPA.ProduitRepository;
import com.gestionst.repositoriesJPA.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class AlerteService {

    @Autowired
    private AlerteRepository alerteRepository;
    
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    
    @Autowired
    private ProduitRepository produitRepository;
    
    private final Random random = new Random();

    public List<Alerte> getAllAlertes() {
        return alerteRepository.findAll();
    }

    public Optional<Alerte> getAlerteById(Long id) {
        return alerteRepository.findById(id);
    }

    public List<Alerte> getAlertesByUtilisateur(Long idUser) {
        return alerteRepository.findByUtilisateurIdUser(idUser);
    }

    public Alerte saveAlerte(Alerte alerte) {
        return alerteRepository.save(alerte);
    }

    public void deleteAlerte(Long id) {
        alerteRepository.deleteById(id);
    }
    
    @Transactional
    public void createAlerteForLowStock(Produit produit) {
        // Vérifier si la quantité est inférieure ou égale à 5
        if (produit != null && produit.getStock() != null && produit.getStock().getQuantiteDisponible() <= 5) {
            // Récupérer tous les utilisateurs pour envoyer l'alerte
            List<Utilisateur> utilisateurs = utilisateurRepository.findAll();
            
            if (!utilisateurs.isEmpty()) {
                // Vérifier si une alerte existe déjà pour ce produit
                List<Alerte> existingAlertes = alerteRepository.findAll();
                boolean alerteExists = false;
                
                for (Alerte existingAlerte : existingAlertes) {
                    if (existingAlerte.getProduit() != null && 
                        existingAlerte.getProduit().getIdProduit() != null && 
                        existingAlerte.getProduit().getIdProduit().equals(produit.getIdProduit())) {
                        alerteExists = true;
                        break;
                    }
                }
                
                // Si aucune alerte n'existe pour ce produit, en créer une nouvelle
                if (!alerteExists) {
                    // Sélectionner aléatoirement un utilisateur pour l'alerte
                    Utilisateur utilisateur = utilisateurs.get(random.nextInt(utilisateurs.size()));
                    
                    // Créer une nouvelle alerte
                    Alerte alerte = new Alerte();
                    alerte.setSeuil(5); // Seuil fixé à 5 comme demandé
                    alerte.setProduit(produit);
                    alerte.setUtilisateur(utilisateur);
                    
                    // Sauvegarder l'alerte
                    alerteRepository.save(alerte);
                    
                    // Ici, on pourrait ajouter un code pour envoyer une notification à l'utilisateur
                    // par exemple par email ou autre moyen de communication
                    System.out.println("Alerte créée pour le produit " + produit.getNom() + " avec quantité " + produit.getStock().getQuantiteDisponible());
                }
            }
        }
    }
    
    @Transactional
    public void checkAndCreateAlertesForAllProducts() {
        // Cette méthode vérifie tous les produits et crée des alertes si nécessaire
        List<Produit> produits = produitRepository.findAll();
        for (Produit produit : produits) {
            if (produit.getStock() != null && produit.getStock().getQuantiteDisponible() <= 5) {
                createAlerteForLowStock(produit);
            }
        }
    }
}
