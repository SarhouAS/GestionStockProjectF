package com.gestionst.services;

import com.gestionst.entites.Stock;
import com.gestionst.entites.Transaction;
import com.gestionst.entites.Utilisateur;
import com.gestionst.repositoriesJPA.StockRepository;
import com.gestionst.repositoriesJPA.TransactionRepository;
import com.gestionst.repositoriesJPA.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private StockRepository stockRepository;
    
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    
    @Autowired
    private AlerteService alerteService;

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    public List<Transaction> getTransactionsByUtilisateur(Long idUser) {
        return transactionRepository.findByUtilisateurIdUser(idUser);
    }

    @Transactional
    public Transaction saveTransaction(Transaction transaction) {
        // Vérifier si l'utilisateur existe
        if (transaction.getUtilisateur() != null && transaction.getUtilisateur().getIdUser() != null) {
            Optional<Utilisateur> utilisateur = utilisateurRepository.findById(transaction.getUtilisateur().getIdUser());
            if (utilisateur.isEmpty()) {
                throw new RuntimeException("Utilisateur non trouvé avec l'ID " + transaction.getUtilisateur().getIdUser());
            }
        }
        
        // Vérifier si le stock existe et mettre à jour la quantité
        if (transaction.getStock() != null && transaction.getStock().getIdStock() != null) {
            Optional<Stock> stockOpt = stockRepository.findById(transaction.getStock().getIdStock());
            if (stockOpt.isPresent()) {
                Stock stock = stockOpt.get();
                
                // Mettre à jour la quantité en fonction du type de transaction
                if ("ENTREE".equalsIgnoreCase(transaction.getType())) {
                    stock.setQuantiteDisponible(stock.getQuantiteDisponible() + transaction.getQuantite());
                } else if ("SORTIE".equalsIgnoreCase(transaction.getType())) {
                    if (stock.getQuantiteDisponible() < transaction.getQuantite()) {
                        throw new RuntimeException("Quantité insuffisante en stock");
                    }
                    stock.setQuantiteDisponible(stock.getQuantiteDisponible() - transaction.getQuantite());
                }
                
                // Sauvegarder le stock mis à jour
                stockRepository.save(stock);
                
                // Vérifier si la quantité est faible après la transaction
                if (stock.getQuantiteDisponible() <= 5) {
                    // Trouver le produit associé à ce stock et créer une alerte
                    alerteService.checkAndCreateAlertesForAllProducts();
                }
            } else {
                throw new RuntimeException("Stock non trouvé avec l'ID " + transaction.getStock().getIdStock());
            }
        }
        
        // Définir la date de la transaction si elle n'est pas déjà définie
        if (transaction.getDate() == null) {
            transaction.setDate(new Date());
        }
        
        // Sauvegarder la transaction
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction updateTransaction(Long id, Transaction transactionDetails) {
        return transactionRepository.findById(id).map(transaction -> {
            // Mettre à jour les champs de base
            transaction.setType(transactionDetails.getType());
            transaction.setDate(transactionDetails.getDate() != null ? transactionDetails.getDate() : transaction.getDate());
            
            // Gérer la mise à jour de la quantité et du stock
            if (transactionDetails.getQuantite() != transaction.getQuantite() && 
                transaction.getStock() != null && transaction.getStock().getIdStock() != null) {
                
                Optional<Stock> stockOpt = stockRepository.findById(transaction.getStock().getIdStock());
                if (stockOpt.isPresent()) {
                    Stock stock = stockOpt.get();
                    
                    // Annuler l'effet de l'ancienne transaction
                    if ("ENTREE".equalsIgnoreCase(transaction.getType())) {
                        stock.setQuantiteDisponible(stock.getQuantiteDisponible() - transaction.getQuantite());
                    } else if ("SORTIE".equalsIgnoreCase(transaction.getType())) {
                        stock.setQuantiteDisponible(stock.getQuantiteDisponible() + transaction.getQuantite());
                    }
                    
                    // Appliquer l'effet de la nouvelle transaction
                    if ("ENTREE".equalsIgnoreCase(transactionDetails.getType())) {
                        stock.setQuantiteDisponible(stock.getQuantiteDisponible() + transactionDetails.getQuantite());
                    } else if ("SORTIE".equalsIgnoreCase(transactionDetails.getType())) {
                        if (stock.getQuantiteDisponible() < transactionDetails.getQuantite()) {
                            throw new RuntimeException("Quantité insuffisante en stock");
                        }
                        stock.setQuantiteDisponible(stock.getQuantiteDisponible() - transactionDetails.getQuantite());
                    }
                    
                    // Sauvegarder le stock mis à jour
                    stockRepository.save(stock);
                    
                    // Vérifier si la quantité est faible après la mise à jour
                    if (stock.getQuantiteDisponible() <= 5) {
                        alerteService.checkAndCreateAlertesForAllProducts();
                    }
                }
            }
            
            // Mettre à jour la quantité
            transaction.setQuantite(transactionDetails.getQuantite());
            
            // Mettre à jour l'utilisateur si nécessaire
            if (transactionDetails.getUtilisateur() != null && transactionDetails.getUtilisateur().getIdUser() != null) {
                Optional<Utilisateur> utilisateur = utilisateurRepository.findById(transactionDetails.getUtilisateur().getIdUser());
                if (utilisateur.isPresent()) {
                    transaction.setUtilisateur(utilisateur.get());
                }
            }
            
            // Mettre à jour le stock si nécessaire
            if (transactionDetails.getStock() != null && transactionDetails.getStock().getIdStock() != null &&
                (transaction.getStock() == null || !transaction.getStock().getIdStock().equals(transactionDetails.getStock().getIdStock()))) {
                
                Optional<Stock> stockOpt = stockRepository.findById(transactionDetails.getStock().getIdStock());
                if (stockOpt.isPresent()) {
                    transaction.setStock(stockOpt.get());
                }
            }
            
            return transactionRepository.save(transaction);
        }).orElseThrow(() -> new RuntimeException("Transaction non trouvée avec l'ID " + id));
    }

    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }
}
