package com.gestionst.services;

import com.gestionst.entites.Categorie;
import com.gestionst.entites.Produit;
import com.gestionst.entites.Stock;
import com.gestionst.repositoriesJPA.CategorieRepository;
import com.gestionst.repositoriesJPA.ProduitRepository;
import com.gestionst.repositoriesJPA.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProduitService {
    @Autowired
    private ProduitRepository produitRepository;
    
    @Autowired
    private CategorieRepository categorieRepository;
    
    @Autowired
    private StockRepository stockRepository;
    
    @Autowired
    private AlerteService alerteService;

    public List<Produit> getAllProduits() {
        return produitRepository.findAll();
    }

    public Optional<Produit> getProduitById(Long id) {
        return produitRepository.findById(id);
    }

    @Transactional
    public Produit saveProduit(Produit produit) {
        // Avec l'annotation CascadeType.ALL sur les relations, nous n'avons plus besoin
        // de sauvegarder explicitement les entités associées avant de sauvegarder le produit.
        // Cependant, nous conservons la logique pour vérifier si les entités existent déjà.
        
        // Vérifier si la catégorie existe, sinon en créer une nouvelle
        if (produit.getCategorie() != null) {
            if (produit.getCategorie().getIdCategorie() != null) {
                Optional<Categorie> existingCategorie = categorieRepository.findById(produit.getCategorie().getIdCategorie());
                if (existingCategorie.isPresent()) {
                    produit.setCategorie(existingCategorie.get());
                }
                // Si la catégorie n'existe pas, elle sera créée automatiquement grâce à CascadeType.ALL
            }
            // Si seulement le type est fourni, une nouvelle catégorie sera créée automatiquement
        }

        // Vérifier si le stock existe, sinon en créer un nouveau
        if (produit.getStock() != null) {
            if (produit.getStock().getIdStock() != null) {
                Optional<Stock> existingStock = stockRepository.findById(produit.getStock().getIdStock());
                if (existingStock.isPresent()) {
                    produit.setStock(existingStock.get());
                }
                // Si le stock n'existe pas, il sera créé automatiquement grâce à CascadeType.ALL
            }
            // Si seulement la quantité est fournie, un nouveau stock sera créé automatiquement
        } else {
            // Créer un stock par défaut si aucun stock n'est fourni
            Stock newStock = new Stock();
            newStock.setQuantiteDisponible(0);
            produit.setStock(newStock);
        }

        // Sauvegarder le produit (et ses entités associées grâce à CascadeType.ALL)
        Produit savedProduit = produitRepository.save(produit);
        
        // Vérifier si la quantité est faible et créer une alerte si nécessaire
        if (savedProduit.getStock() != null && savedProduit.getStock().getQuantiteDisponible() <= 5) {
            alerteService.createAlerteForLowStock(savedProduit);
        }
        
        return savedProduit;
    }

    @Transactional
    public Produit updateProduit(Long id, Produit newProduit) {
        return produitRepository.findById(id).map(produit -> {
            // Ne mettre à jour le nom que s'il n'est pas null
            if (newProduit.getNom() != null) {
                produit.setNom(newProduit.getNom());
            }
            
            // Ne mettre à jour le prix que s'il est différent de 0 (valeur par défaut)
            if (newProduit.getPrix() != 0) {
                produit.setPrix(newProduit.getPrix());
            }
            
            // Mise à jour de la catégorie si nécessaire
            if (newProduit.getCategorie() != null) {
                if (newProduit.getCategorie().getIdCategorie() != null) {
                    Optional<Categorie> existingCategorie = categorieRepository.findById(newProduit.getCategorie().getIdCategorie());
                    if (existingCategorie.isPresent()) {
                        produit.setCategorie(existingCategorie.get());
                    } else {
                        // Si la catégorie n'existe pas, on utilise celle fournie (sera créée automatiquement)
                        produit.setCategorie(newProduit.getCategorie());
                    }
                } else if (newProduit.getCategorie().getType() != null && !newProduit.getCategorie().getType().isEmpty()) {
                    // Créer une nouvelle catégorie si seulement le type est fourni
                    Categorie newCategorie = new Categorie();
                    newCategorie.setType(newProduit.getCategorie().getType());
                    produit.setCategorie(newCategorie);
                }
            }
            
            // Mise à jour du stock si nécessaire
            if (newProduit.getStock() != null) {
                if (newProduit.getStock().getIdStock() != null) {
                    Optional<Stock> existingStock = stockRepository.findById(newProduit.getStock().getIdStock());
                    if (existingStock.isPresent()) {
                        Stock stock = existingStock.get();
                        stock.setQuantiteDisponible(newProduit.getStock().getQuantiteDisponible());
                        produit.setStock(stock);
                    } else {
                        // Si le stock n'existe pas, on utilise celui fourni (sera créé automatiquement)
                        produit.setStock(newProduit.getStock());
                    }
                } else {
                    // Mettre à jour le stock existant ou en créer un nouveau
                    if (produit.getStock() != null) {
                        produit.getStock().setQuantiteDisponible(newProduit.getStock().getQuantiteDisponible());
                    } else {
                        Stock newStock = new Stock();
                        newStock.setQuantiteDisponible(newProduit.getStock().getQuantiteDisponible());
                        produit.setStock(newStock);
                    }
                }
            }
            
            // Sauvegarder le produit mis à jour
            Produit updatedProduit = produitRepository.save(produit);
            
            // Vérifier si la quantité est faible et créer une alerte si nécessaire
            // Cette vérification est déplacée ici pour s'assurer qu'elle est toujours exécutée
            if (updatedProduit.getStock() != null && updatedProduit.getStock().getQuantiteDisponible() <= 5) {
                alerteService.createAlerteForLowStock(updatedProduit);
            }
            
            return updatedProduit;
        }).orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'ID " + id));
    }

    public void deleteProduit(Long id) {
        produitRepository.deleteById(id);
    }
    
    public List<Produit> getProduitsByCategorie(Long idCategorie) {
        return produitRepository.findByCategorieIdCategorie(idCategorie);
    }
}
