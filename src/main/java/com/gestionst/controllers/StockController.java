package com.gestionst.controllers;

import com.gestionst.entites.Stock;
import com.gestionst.services.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/stocks")
@CrossOrigin("*")
public class StockController {

    @Autowired
    private StockService stockService;

    @GetMapping
    public List<Stock> getAllStocks() {
        return stockService.getAllStocks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Stock> getStockById(@PathVariable Long id) {
        Optional<Stock> stock = stockService.getStockById(id);
        return stock.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Stock> createStock(@RequestBody Stock stock) {
        try {
            Stock nouveauStock = stockService.saveStock(stock);
            return new ResponseEntity<>(nouveauStock, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Stock> updateStock(@PathVariable Long id, @RequestBody Stock stock) {
        try {
            Stock updatedStock = stockService.updateStock(id, stock);
            return ResponseEntity.ok(updatedStock);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteStock(@PathVariable Long id) {
        try {
            stockService.deleteStock(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PutMapping("/{id}/ajouter")
    public ResponseEntity<Stock> ajouterQuantite(@PathVariable Long id, @RequestParam int quantite) {
        try {
            Stock updatedStock = stockService.ajouterQuantite(id, quantite);
            return ResponseEntity.ok(updatedStock);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PutMapping("/{id}/retirer")
    public ResponseEntity<Stock> retirerQuantite(@PathVariable Long id, @RequestParam int quantite) {
        try {
            Stock updatedStock = stockService.retirerQuantite(id, quantite);
            return ResponseEntity.ok(updatedStock);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Quantité insuffisante")) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
