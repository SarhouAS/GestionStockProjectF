package com.gestionst.controllers;

import com.gestionst.entites.Alerte;
import com.gestionst.services.AlerteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/alertes")
@CrossOrigin("*")
public class AlerteController {

    @Autowired
    private AlerteService alerteService;

    @GetMapping
    public List<Alerte> getAllAlertes() {
        return alerteService.getAllAlertes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alerte> getAlerteById(@PathVariable Long id) {
        Optional<Alerte> alerte = alerteService.getAlerteById(id);
        return alerte.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/utilisateur/{idUser}")
    public List<Alerte> getAlertesByUtilisateur(@PathVariable Long idUser) {
        return alerteService.getAlertesByUtilisateur(idUser);
    }

    @PostMapping
    public ResponseEntity<Alerte> createAlerte(@RequestBody Alerte alerte) {
        try {
            Alerte nouvelleAlerte = alerteService.saveAlerte(alerte);
            return new ResponseEntity<>(nouvelleAlerte, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteAlerte(@PathVariable Long id) {
        try {
            alerteService.deleteAlerte(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
