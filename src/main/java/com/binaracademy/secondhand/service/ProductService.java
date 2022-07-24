package com.binaracademy.secondhand.service;

import com.binaracademy.secondhand.dto.ProductUploadDto;
import com.binaracademy.secondhand.model.Offer;
import com.binaracademy.secondhand.model.Product;
import com.binaracademy.secondhand.model.ProductImage;
import java.util.List;

public interface ProductService {
    Product getProduct(Long productId);
    List<Product> getSellerProducts(String email, int page, int size);
    List<Product> getAllProducts(String search, int page, int size);
    List<Product> getProductsByCategory(Long categoryId, int page, int size);
    List<ProductImage> getAllProductImages(Long productId);
    List<Offer> getAllProductOffers(Long productId);
    Product saveProduct(String username, ProductUploadDto uploadproductDto);
    Product updateProduct(String username, Long productId, ProductUploadDto uploadproductDto);
    Product setProductSold(Long productId);
    boolean deleteProduct(String username, Long productId);
}
