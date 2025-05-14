package com.gestionst.services;

import com.gestionst.entites.Role;
import com.gestionst.repositoriesJPA.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }
    
    public Optional<Role> getRoleByLibelle(String libelle) {
        return roleRepository.findByLibelle(libelle);
    }
    
    public Role getOrCreateUserRole() {
        // Chercher le rôle USER
        Optional<Role> userRole = roleRepository.findByLibelle("USER");
        
        // Si le rôle existe, le retourner
        if (userRole.isPresent()) {
            return userRole.get();
        }
        
        // Sinon, créer un nouveau rôle USER
        Role newUserRole = new Role();
        newUserRole.setLibelle("USER");
        return roleRepository.save(newUserRole);
    }

    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    public Role updateRole(Long id, Role roleDetails) {
        return roleRepository.findById(id).map(role -> {
            role.setLibelle(roleDetails.getLibelle());
            return roleRepository.save(role);
        }).orElseThrow(() -> new RuntimeException("Role non trouvé avec l'ID " + id));
    }

    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }
}
