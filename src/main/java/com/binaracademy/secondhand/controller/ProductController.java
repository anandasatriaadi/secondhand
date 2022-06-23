package com.binaracademy.secondhand.controller;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.binaracademy.secondhand.dto.UploadProductDto;
import com.binaracademy.secondhand.model.Product;
import com.binaracademy.secondhand.model.ProductImage;
import com.binaracademy.secondhand.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class ProductController {
    private final ProductService productService;
    
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/product/{id}/images")
    public ResponseEntity<List<ProductImage>> getAllProductImages(@PathVariable("id") Long id) {
        return ResponseEntity.ok(productService.getAllProductImages(id));
    }

    @PostMapping("/product/save")
    public ResponseEntity<String> saveProduct(Authentication authentication, @ModelAttribute UploadProductDto uploadProductDto, @RequestParam("images") MultipartFile[] images) {
        log.info(authentication.getPrincipal().toString() + " saving a product.");

        // ======== Return Bad Request on Incomplete Request ========
        try {
            log.info(Arrays.toString(images));
            productService.saveProduct(authentication.getPrincipal().toString(), uploadProductDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        // ======== Return Created on User Registration Success ========
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/register").toUriString());
        return ResponseEntity.created(uri).body("Product saved successfully.");
    }
}
