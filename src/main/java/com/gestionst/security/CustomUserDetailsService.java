package com.gestionst.security;

import com.gestionst.entites.Utilisateur;
import com.gestionst.services.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UtilisateurService utilisateurService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Utilisateur> utilisateurOpt = utilisateurService.findByEmail(email);
        
        if (utilisateurOpt.isEmpty()) {
            throw new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email);
        }
        
        Utilisateur utilisateur = utilisateurOpt.get();
        
        String role = utilisateur.getRole() != null ? utilisateur.getRole().getLibelle() : "USER";
        
        return new User(
            utilisateur.getEmail(),
            utilisateur.getMdp(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }
}
