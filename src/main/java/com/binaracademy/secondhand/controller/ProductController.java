package com.binaracademy.secondhand.controller;

import com.binaracademy.secondhand.dto.RestDto;
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
import org.springframework.web.server.ResponseStatusException;
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

    // ========================================================================
    //   Get products with search and pagination
    // ========================================================================
    @GetMapping("/products")
    public ResponseEntity<RestDto> getAllProducts(
        @RequestParam(defaultValue = "", required = false) String search,
        @RequestParam("page") int page,
        @RequestParam("size") int size
    ) {
        if (!search.equals("")) {
            String[] texts = search.replace(" ", "").split("");
            search = String.join("%", texts);
        }

        try {
            List<Product> result = productService.getAllProducts(search, page, size);
            return ResponseEntity.ok(new RestDto(200, "ok", result));
        } catch (ResponseStatusException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(e.getStatus()).body(new RestDto(e.getRawStatusCode(), e.getMessage(), ""));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body(new RestDto(500, "Internal Server Error", ""));
        }
    }

    // ========================================================================
    //   Get products by category
    // ========================================================================
    @GetMapping("/product/category/{categoryId}")
    public ResponseEntity<RestDto> getProductsByCategory(
        @PathVariable Long categoryId,
        @RequestParam("page") int page,
        @RequestParam("size") int size
    ) {
        try {
            List<Product> result = productService.getProductsByCategory(categoryId, page, size);
            return ResponseEntity.ok(new RestDto(200, "ok", result));
        } catch (ResponseStatusException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(e.getStatus()).body(new RestDto(e.getRawStatusCode(), e.getMessage(), ""));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body(new RestDto(500, "Internal Server Error", ""));
        }
    }

    // ========================================================================
    //   Get product images
    // ========================================================================
    @GetMapping("/product/{id}/images")
    public ResponseEntity<List<ProductImage>> getAllProductImages(@PathVariable("id") Long id) {
        return ResponseEntity.ok(productService.getAllProductImages(id));
    }

    // ========================================================================
    //   Create/save product
    // ========================================================================
    @PostMapping("/product/save")
    public ResponseEntity<RestDto> saveProduct(Authentication authentication, @ModelAttribute UploadProductDto uploadProductDto) {
        try {
            productService.saveProduct(authentication.getPrincipal().toString(), uploadProductDto);
            log.info(MSG_SUCCESS + authentication.getPrincipal().toString() + " saving product");
        } catch (ResponseStatusException e) {
            log.info(MSG_FAILED + authentication.getPrincipal().toString() + " saving product");
            return ResponseEntity.status(e.getStatus()).body(new RestDto(e.getRawStatusCode(), e.getMessage(), ""));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(new RestDto(500, "Internal Server Error", ""));
        }

        // ======== Return Created on User Registration Success ========
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/product/save").toUriString());
        return ResponseEntity.created(uri).body(new RestDto(200, "ok", "Product saved successfully"));
    }

    // ========================================================================
    //   Update product
    // ========================================================================
    @PutMapping("/product/{id}/update")
    public ResponseEntity<RestDto> updateProduct(
        Authentication authentication,
        @PathVariable Long id,
        @ModelAttribute UploadProductDto uploadProductDto
    ) {
        try {
            productService.updateProduct(authentication.getPrincipal().toString(), id, uploadProductDto);
            log.info(MSG_SUCCESS + authentication.getPrincipal().toString() + " updating product");
        } catch (ResponseStatusException e) {
            log.info(MSG_FAILED + authentication.getPrincipal().toString() + " updating product");
            return ResponseEntity.status(e.getStatus()).body(new RestDto(e.getRawStatusCode(), e.getMessage(), ""));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(new RestDto(500, "Internal Server Error", ""));
        }

        // ======== Return Created on User Registration Success ========
        URI uri = URI.create(
            ServletUriComponentsBuilder.fromCurrentContextPath().path(String.format("/api/product/%s/update", id)).toUriString()
        );
        return ResponseEntity.created(uri).body(new RestDto(200, "ok", "Product updated successfully"));
    }

    // ========================================================================
    //   Delete product
    // ========================================================================
    @DeleteMapping("/product/{id}/delete")
    public ResponseEntity<RestDto> deleteProduct(Authentication authentication, @PathVariable Long id) {
        try {
            productService.deleteProduct(authentication.getPrincipal().toString(), id);
            log.info(MSG_SUCCESS + authentication.getPrincipal().toString() + " deleting product");
        } catch (ResponseStatusException e) {
            log.info(MSG_FAILED + authentication.getPrincipal().toString() + " deleting product");
            return ResponseEntity.status(e.getStatus()).body(new RestDto(e.getRawStatusCode(), e.getMessage(), ""));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(new RestDto(500, "Internal Server Error", ""));
        }

        return ResponseEntity.ok().body(new RestDto(200, "Product deleted successfully", ""));
    }
}
