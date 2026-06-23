package edu.eci.ahia.service;

import org.springframework.stereotype.Service;
import edu.eci.ahia.repository.ProductRepository;
import edu.eci.ahia.exception.ProductNotFoundException;
import edu.eci.ahia.exception.InsufficientStockException;
import edu.eci.ahia.model.entity.Product;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product createProduct(Product product){

        Product newProduct = Product.builder()
            .name(product.getName())
            .units(product.getUnits())
            .price(product.getPrice())
            .build();

        Product productToSave = productRepository.save(newProduct);

        return productToSave;
    }

    public Product updateProductPrice(Long id, double price){

        Product productToUpdate = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product with id: " + id + " not found "));

        productToUpdate.setPrice(price);

        Product productUpdated = productRepository.save(productToUpdate);

        return productUpdated;


    }

    public Product updateProductUnits(Long id, int units){

        Product productToUpdate = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product with id: " + id + " not found"));

        productToUpdate.setUnits(units);
        
        Product productUpdated = productRepository.save(productToUpdate);

        return productUpdated;

    }

    public void reduceUnits(Long id, int units) {

        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product with id: " + id + " not found"));

        int initialUnits = product.getUnits();

        if (units <= 0) {
            throw new InsufficientStockException("Units to reduce must be greater than zero");
        }

        if (units > initialUnits) {
            throw new InsufficientStockException(
                "Insufficient stock for product with id: " + id + ". Available: " + initialUnits + ", requested: " + units);
        }

        int newUnits = initialUnits - units;

        product.setUnits(newUnits);

        productRepository.save(product);

    }

    public void deleteProduct(Long id) {
        Product productToDelete = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product to delete with id : " + id + " not found"));

        productRepository.delete(productToDelete);

    }





}
