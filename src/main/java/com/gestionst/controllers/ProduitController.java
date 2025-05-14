package com.gestionst.controllers;

import com.gestionst.entites.Produit;
import com.gestionst.services.ProduitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/produits")
public class ProduitController {
    @Autowired
    private ProduitService produitService;

    @GetMapping
    public List<Produit> getAllProduits() {
        return produitService.getAllProduits();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produit> getProduitById(@PathVariable Long id) {
        Optional<Produit> produit = produitService.getProduitById(id);
        return produit.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createProduit(@RequestBody Produit produit) {
        if (produit.getStock() == null) {
            produit.setStock(null);
        }

        if (produit.getCategorie() == null) {
            produit.setCategorie(null);
        }

        if (produit.getPrix() == 0.0) {
            produit.setPrix(0.0);
        }

        produitService.saveProduit(produit);

        return ResponseEntity.ok("Produit enregistré avec succès.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produit> updateProduit(@PathVariable Long id, @RequestBody Produit newProduit) {
        try {
            Produit updatedProduit = produitService.updateProduit(id, newProduit);
            return ResponseEntity.ok(updatedProduit);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduit(@PathVariable Long id) {
        produitService.deleteProduit(id);
        return ResponseEntity.noContent().build();
    }
    
}
