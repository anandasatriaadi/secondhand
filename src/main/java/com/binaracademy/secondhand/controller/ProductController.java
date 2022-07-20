package com.binaracademy.secondhand.controller;

import com.binaracademy.secondhand.dto.ProductResponseImageDto;
import com.binaracademy.secondhand.dto.ProductUploadDto;
import com.binaracademy.secondhand.dto.RestResponseDto;
import com.binaracademy.secondhand.model.Notification;
import com.binaracademy.secondhand.model.Product;
import com.binaracademy.secondhand.model.ProductImage;
import com.binaracademy.secondhand.service.NotificationService;
import com.binaracademy.secondhand.service.ProductImageService;
import com.binaracademy.secondhand.service.ProductService;
import com.binaracademy.secondhand.util.enums.NotificationType;
import java.net.URI;
import java.time.LocalDateTime;
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
    private final String INTERNAL_ERROR_MSG = "Internal Server Error";

    @Autowired
    private final ProductService productService;

    @Autowired
    private final ProductImageService productImageService;

    @Autowired
    private final NotificationService notificationService;

    // ========================================================================
    //   Get seller products list
    // ========================================================================
    @GetMapping("/products/seller")
    public ResponseEntity<RestResponseDto> getSellerProducts(Authentication authentication) {
        try {
            List<Product> result = productService.getSellerProducts(authentication.getPrincipal().toString());
            return ResponseEntity.ok(new RestResponseDto(200, "ok", result));
        } catch (ResponseStatusException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(e.getStatus()).body(new RestResponseDto(e.getRawStatusCode(), e.getReason(), ""));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body(new RestResponseDto(500, INTERNAL_ERROR_MSG, ""));
        }
    }

    // ========================================================================
    //   Get products with search and pagination
    // ========================================================================
    @GetMapping("/products")
    public ResponseEntity<RestResponseDto> getAllProducts(
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
            return ResponseEntity.ok(new RestResponseDto(200, "ok", result));
        } catch (ResponseStatusException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(e.getStatus()).body(new RestResponseDto(e.getRawStatusCode(), e.getReason(), ""));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body(new RestResponseDto(500, INTERNAL_ERROR_MSG, ""));
        }
    }

    // ========================================================================
    //   Get products by category
    // ========================================================================
    @GetMapping("/products/category/{categoryId}")
    public ResponseEntity<RestResponseDto> getProductsByCategory(
        @PathVariable Long categoryId,
        @RequestParam("page") int page,
        @RequestParam("size") int size
    ) {
        try {
            List<Product> result = productService.getProductsByCategory(categoryId, page, size);
            return ResponseEntity.ok(new RestResponseDto(200, "ok", result));
        } catch (ResponseStatusException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(e.getStatus()).body(new RestResponseDto(e.getRawStatusCode(), e.getReason(), ""));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body(new RestResponseDto(500, INTERNAL_ERROR_MSG, ""));
        }
    }

    // ========================================================================
    //   Get product images
    // ========================================================================
    @GetMapping("/product/{id}/images")
    public ResponseEntity<RestResponseDto> getAllProductImages(@PathVariable("id") Long id) {
        try {
            List<ProductImage> result = productService.getAllProductImages(id);
            return ResponseEntity.ok(new RestResponseDto(200, "ok", result));
        } catch (ResponseStatusException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(e.getStatus()).body(new RestResponseDto(e.getRawStatusCode(), e.getReason(), ""));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body(new RestResponseDto(500, INTERNAL_ERROR_MSG, ""));
        }
    }

    // ========================================================================
    //   Get product detail
    // ========================================================================
    @GetMapping("/product/{id}/detail")
    public ResponseEntity<RestResponseDto> getProductDetail(@PathVariable("id") Long id) {
        try {
            Product prodResult = productService.getProduct(id);
            List<ProductImage> prodImageResult = productImageService.getProductImage(id);

            ProductResponseImageDto response = new ProductResponseImageDto();
            response.setId(prodResult.getId());
            response.setName(prodResult.getName());
            response.setDescription(prodResult.getDescription());
            response.setPrice(prodResult.getPrice());
            response.setAddress(prodResult.getAddress());
            response.setUserId(prodResult.getUserId());
            response.setCategoryId(prodResult.getCategoryId());
            response.setProductImages(prodImageResult);

            return ResponseEntity.ok(new RestResponseDto(200, "ok", response));
        } catch (ResponseStatusException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(e.getStatus()).body(new RestResponseDto(e.getRawStatusCode(), e.getReason(), ""));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body(new RestResponseDto(500, INTERNAL_ERROR_MSG, ""));
        }
    }

    // ========================================================================
    //   Create/save product
    // ========================================================================
    @PostMapping("/product/save")
    public ResponseEntity<RestResponseDto> saveProduct(Authentication authentication, @ModelAttribute ProductUploadDto uploadProductDto) {
        try {
            Product saveResult = productService.saveProduct(authentication.getPrincipal().toString(), uploadProductDto);

            Notification publishedNotif = new Notification();
            publishedNotif.setUserId(saveResult.getUserId());
            publishedNotif.setType(NotificationType.PUBLISH);
            publishedNotif.setCreatedAt(LocalDateTime.now());

            notificationService.addNotification(publishedNotif);

            log.info(MSG_SUCCESS + authentication.getPrincipal().toString() + " saving product");
        } catch (ResponseStatusException e) {
            log.info(MSG_FAILED + authentication.getPrincipal().toString() + " saving product");
            return ResponseEntity.status(e.getStatus()).body(new RestResponseDto(e.getRawStatusCode(), e.getReason(), ""));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new RestResponseDto(500, INTERNAL_ERROR_MSG, ""));
        }

        // ======== Return Created on User Registration Success ========
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/product/save").toUriString());
        return ResponseEntity.created(uri).body(new RestResponseDto(200, "ok", "Product saved successfully"));
    }

    // ========================================================================
    //   Update product
    // ========================================================================
    @PutMapping("/product/{id}/update")
    public ResponseEntity<RestResponseDto> updateProduct(
        Authentication authentication,
        @PathVariable Long id,
        @ModelAttribute ProductUploadDto uploadProductDto
    ) {
        try {
            productService.updateProduct(authentication.getPrincipal().toString(), id, uploadProductDto);
            log.info(MSG_SUCCESS + authentication.getPrincipal().toString() + " updating product");
        } catch (ResponseStatusException e) {
            log.info(MSG_FAILED + authentication.getPrincipal().toString() + " updating product");
            return ResponseEntity.status(e.getStatus()).body(new RestResponseDto(e.getRawStatusCode(), e.getReason(), ""));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(new RestResponseDto(500, INTERNAL_ERROR_MSG, ""));
        }

        // ======== Return Created on User Registration Success ========
        URI uri = URI.create(
            ServletUriComponentsBuilder.fromCurrentContextPath().path(String.format("/api/product/%s/update", id)).toUriString()
        );
        return ResponseEntity.created(uri).body(new RestResponseDto(200, "ok", "Product updated successfully"));
    }

    // ========================================================================
    //   Update product status to sold
    // ========================================================================
    @GetMapping("/product/{id}/sold")
    public ResponseEntity<RestResponseDto> setProductSold(@PathVariable Long id) {
        try {
            productService.setProductSold(id);
            log.info(MSG_SUCCESS + " sold product");
        } catch (ResponseStatusException e) {
            log.info(MSG_FAILED + " sold product");
            return ResponseEntity.status(e.getStatus()).body(new RestResponseDto(e.getRawStatusCode(), e.getReason(), ""));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(new RestResponseDto(500, INTERNAL_ERROR_MSG, ""));
        }

        // ======== Return Created on User Registration Success ========
        URI uri = URI.create(
            ServletUriComponentsBuilder.fromCurrentContextPath().path(String.format("/api/product/%s/sold", id)).toUriString()
        );
        return ResponseEntity.created(uri).body(new RestResponseDto(200, "ok", "Product updated successfully"));
    }

    // ========================================================================
    //   Delete product
    // ========================================================================
    @DeleteMapping("/product/{id}/delete")
    public ResponseEntity<RestResponseDto> deleteProduct(Authentication authentication, @PathVariable Long id) {
        try {
            productService.deleteProduct(authentication.getPrincipal().toString(), id);
            log.info(MSG_SUCCESS + authentication.getPrincipal().toString() + " deleting product");
        } catch (ResponseStatusException e) {
            log.info(MSG_FAILED + authentication.getPrincipal().toString() + " deleting product");
            return ResponseEntity.status(e.getStatus()).body(new RestResponseDto(e.getRawStatusCode(), e.getReason(), ""));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(new RestResponseDto(500, INTERNAL_ERROR_MSG, ""));
        }

        return ResponseEntity.ok().body(new RestResponseDto(200, "Product deleted successfully", ""));
    }
}
