package com.binaracademy.secondhand.controller;

import com.binaracademy.secondhand.dto.UploadProductDto;
import com.binaracademy.secondhand.model.Product;
import com.binaracademy.secondhand.model.ProductImage;
import com.binaracademy.secondhand.service.ProductService;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class ProductController {

    private final String MSG_SUCCESS = "Success - ";
    private final String MSG_FAILED = "Failed - ";

    @Autowired
    private final ProductService productService;

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts(@RequestParam("page") int page, @RequestParam("size") int size) {
        return ResponseEntity.ok(productService.getAllProducts(page, size));
    }

    @GetMapping("/product/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategory(
        @PathVariable Long categoryId,
        @RequestParam("page") int page,
        @RequestParam("size") int size
    ) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId, page, size));
    }

    @GetMapping("/product/{id}/images")
    public ResponseEntity<List<ProductImage>> getAllProductImages(@PathVariable("id") Long id) {
        return ResponseEntity.ok(productService.getAllProductImages(id));
    }

    @PostMapping("/product/save")
    public ResponseEntity<String> saveProduct(Authentication authentication, @ModelAttribute UploadProductDto uploadProductDto) {
        // ======== Return Bad Request on Incomplete Request ========
        try {
            productService.saveProduct(authentication.getPrincipal().toString(), uploadProductDto);
            log.info(MSG_SUCCESS + authentication.getPrincipal().toString() + " saving product");
        } catch (Exception e) {
            log.info(MSG_FAILED + authentication.getPrincipal().toString() + " saving product");
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        // ======== Return Created on User Registration Success ========
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/product/save").toUriString());
        return ResponseEntity.created(uri).body("Product saved successfully.");
    }

    @PutMapping("/product/{id}/update")
    public ResponseEntity<String> updateProduct(
        Authentication authentication,
        @PathVariable Long id,
        @ModelAttribute UploadProductDto uploadProductDto
    ) {
        // ======== Return Bad Request on Incomplete Request ========
        try {
            productService.updateProduct(authentication.getPrincipal().toString(), id, uploadProductDto);
            log.info(MSG_SUCCESS + authentication.getPrincipal().toString() + " updating product");
        } catch (Exception e) {
            log.info(MSG_FAILED + authentication.getPrincipal().toString() + " updating product");
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        // ======== Return Created on User Registration Success ========
        URI uri = URI.create(
            ServletUriComponentsBuilder.fromCurrentContextPath().path(String.format("/api/product/%s/update", id)).toUriString()
        );
        return ResponseEntity.created(uri).body("Product updated successfully.");
    }

    @DeleteMapping("/product/{id}/delete")
    public ResponseEntity<String> deleteProduct(Authentication authentication, @PathVariable Long id) {
        try {
            productService.deleteProduct(authentication.getPrincipal().toString(), id);
            log.info(MSG_SUCCESS + authentication.getPrincipal().toString() + " deleting product");
        } catch (Exception e) {
            log.info(MSG_FAILED + authentication.getPrincipal().toString() + " deleting product");
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().body("Product deleted successfully");
    }
}
