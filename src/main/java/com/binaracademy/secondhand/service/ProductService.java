package com.binaracademy.secondhand.service;

import com.binaracademy.secondhand.dto.UploadProductDto;
import com.binaracademy.secondhand.model.Product;
import com.binaracademy.secondhand.model.ProductImage;
import com.binaracademy.secondhand.model.ProductOffer;
import java.util.List;

public interface ProductService {
    Product saveProduct(String username, UploadProductDto uploadproductDto);
    Product updateProduct(String username, Long productId, UploadProductDto uploadproductDto);
    Product getProduct(Long productId);
    List<ProductOffer> getAllProductOffers(Long productId);
    List<ProductImage> getAllProductImages(Long productId);
    List<Product> getAllProducts(int page, int size);
    List<Product> getProductsByCategory(Long categoryId, int page, int size);
    boolean deleteProduct(String username, Long productId);
}
