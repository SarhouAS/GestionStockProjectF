package com.gestionst.services;

import com.gestionst.entites.Stock;
import com.gestionst.repositoriesJPA.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;
    
    @Autowired
    private AlerteService alerteService;

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public Optional<Stock> getStockById(Long id) {
        return stockRepository.findById(id);
    }

    public Stock saveStock(Stock stock) {
        Stock savedStock = stockRepository.save(stock);
        
        // Vérifier si la quantité est faible et créer une alerte si nécessaire
        if (savedStock.getQuantiteDisponible() <= 5) {
            alerteService.checkAndCreateAlertesForAllProducts();
        }
        
        return savedStock;
    }

    public Stock updateStock(Long id, Stock stockDetails) {
        return stockRepository.findById(id).map(stock -> {
            stock.setQuantiteDisponible(stockDetails.getQuantiteDisponible());
            
            Stock updatedStock = stockRepository.save(stock);
            
            // Vérifier si la quantité est faible et créer une alerte si nécessaire
            if (updatedStock.getQuantiteDisponible() <= 5) {
                alerteService.checkAndCreateAlertesForAllProducts();
            }
            
            return updatedStock;
        }).orElseThrow(() -> new RuntimeException("Stock non trouvé avec l'ID " + id));
    }

    public void deleteStock(Long id) {
        stockRepository.deleteById(id);
    }
    
    public Stock ajouterQuantite(Long id, int quantite) {
        return stockRepository.findById(id).map(stock -> {
            stock.setQuantiteDisponible(stock.getQuantiteDisponible() + quantite);
            
            Stock updatedStock = stockRepository.save(stock);
            
            return updatedStock;
        }).orElseThrow(() -> new RuntimeException("Stock non trouvé avec l'ID " + id));
    }
    
    public Stock retirerQuantite(Long id, int quantite) {
        return stockRepository.findById(id).map(stock -> {
            if (stock.getQuantiteDisponible() < quantite) {
                throw new RuntimeException("Quantité insuffisante en stock");
            }
            
            stock.setQuantiteDisponible(stock.getQuantiteDisponible() - quantite);
            
            Stock updatedStock = stockRepository.save(stock);
            
            // Vérifier si la quantité est faible et créer une alerte si nécessaire
            if (updatedStock.getQuantiteDisponible() <= 5) {
                alerteService.checkAndCreateAlertesForAllProducts();
            }
            
            return updatedStock;
        }).orElseThrow(() -> new RuntimeException("Stock non trouvé avec l'ID " + id));
    }
}
