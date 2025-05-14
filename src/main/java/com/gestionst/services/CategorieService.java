package com.gestionst.services;

import com.gestionst.entites.Categorie;
import com.gestionst.repositoriesJPA.CategorieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategorieService {

    @Autowired
    private CategorieRepository categorieRepository;

    public List<Categorie> getAllCategories() {
        return categorieRepository.findAll();
    }

    public Optional<Categorie> getCategorieById(Long id) {
        return categorieRepository.findById(id);
    }

    public Categorie createCategorie(Categorie categorie) {
        return categorieRepository.save(categorie);
    }

    public void deleteCategorie(Long id) {
        categorieRepository.deleteById(id);
    }
}
