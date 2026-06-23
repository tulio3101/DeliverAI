package edu.eci.ahia.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.eci.ahia.mapper.ProductMapper;
import edu.eci.ahia.model.dto.request.ProductRequestDTO;
import edu.eci.ahia.model.dto.response.ProductResponseDTO;
import edu.eci.ahia.model.entity.Product;
import edu.eci.ahia.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;


    @PostMapping("")
    public ResponseEntity<ProductResponseDTO> createProduct(
        @Valid @RequestBody ProductRequestDTO dto){

        Product productToCreated = productMapper.toEntity(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            productMapper.toDto(productService.createProduct(productToCreated)));

    }

    @PatchMapping("/{id}/price")
    public ResponseEntity<ProductResponseDTO> updateProductPrice(
        @PathVariable Long id, @RequestParam double price) {

            return ResponseEntity.ok(productMapper.toDto(productService.updateProductPrice(id, price)));

        }

    @PatchMapping("/{id}/units")
    public ResponseEntity<ProductResponseDTO> updateProductUnits(
        @PathVariable Long id, @RequestParam int units) {

            return ResponseEntity.ok(productMapper.toDto(productService.updateProductUnits(id, units)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
        @PathVariable Long id) {

            productService.deleteProduct(id);

            return ResponseEntity.noContent().build();

        }

}