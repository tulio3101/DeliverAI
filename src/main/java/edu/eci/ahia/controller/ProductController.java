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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Operations related to products")
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;


    @PostMapping("")
    @Operation(summary = "Create a product", description = "Creates a new product in the inventory")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    public ResponseEntity<ProductResponseDTO> createProduct(
        @Valid @RequestBody ProductRequestDTO dto){

        Product productToCreated = productMapper.toEntity(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            productMapper.toDto(productService.createProduct(productToCreated)));

    }

    @PatchMapping("/{id}/price")
    @Operation(summary = "Update product price", description = "Updates the price of an existing product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Price updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    public ResponseEntity<ProductResponseDTO> updateProductPrice(
        @Parameter(description = "Product ID", example = "1") @PathVariable Long id,
        @Parameter(description = "New price", example = "15.99") @RequestParam double price) {

            return ResponseEntity.ok(productMapper.toDto(productService.updateProductPrice(id, price)));

        }

    @PatchMapping("/{id}/units")
    @Operation(summary = "Update product units", description = "Updates the stock quantity of an existing product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Units updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    public ResponseEntity<ProductResponseDTO> updateProductUnits(
        @Parameter(description = "Product ID", example = "1") @PathVariable Long id,
        @Parameter(description = "New units count", example = "100") @RequestParam int units) {

            return ResponseEntity.ok(productMapper.toDto(productService.updateProductUnits(id, units)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product", description = "Deletes an existing product by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Product deleted successfully", content = @Content),
        @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    public ResponseEntity<Void> deleteProduct(
        @Parameter(description = "Product ID", example = "1") @PathVariable Long id) {

            productService.deleteProduct(id);

            return ResponseEntity.noContent().build();

        }

}
