package com.gestionst.repositoriesJPA;

import com.gestionst.entites.Alerte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlerteRepository extends JpaRepository<Alerte, Long> {
    List<Alerte> findByUtilisateurIdUser(Long idUser);
}
