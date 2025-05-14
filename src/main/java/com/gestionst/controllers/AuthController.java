package com.gestionst.controllers;

import com.gestionst.entites.Utilisateur;
import com.gestionst.security.JwtService;
import com.gestionst.services.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth") 
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // Rechercher l'utilisateur par email
        Optional<Utilisateur> utilisateurOpt = utilisateurService.findByEmail(loginRequest.getEmail());
        
        if (utilisateurOpt.isPresent()) {
            Utilisateur utilisateur = utilisateurOpt.get();
            
            // Vérifier le mot de passe
            if (utilisateur.getMdp().equals(loginRequest.getPassword())) {
                // Générer le token JWT
                String token = jwtService.generateToken(utilisateur);
                
                // Créer la réponse
                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("user", utilisateur);
                
                return ResponseEntity.ok(response);
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou mot de passe incorrect");
    }

    // Classe pour la requête de connexion
    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
