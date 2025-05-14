package com.gestionst.repositoriesJPA;

import com.gestionst.entites.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {
    List<Produit> findByCategorieIdCategorie(Long idCategorie);
}
