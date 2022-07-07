package com.binaracademy.secondhand.service;

import com.binaracademy.secondhand.dto.UploadProductDto;
import com.binaracademy.secondhand.model.Product;
import com.binaracademy.secondhand.model.ProductImage;
import com.binaracademy.secondhand.model.ProductOffer;
import java.util.List;

public interface ProductService {
    Product getProduct(Long productId);
    List<Product> getAllProducts(String search, int page, int size);
    List<Product> getProductsByCategory(Long categoryId, int page, int size);
    List<ProductImage> getAllProductImages(Long productId);
    List<ProductOffer> getAllProductOffers(Long productId);
    Product saveProduct(String username, UploadProductDto uploadproductDto);
    Product updateProduct(String username, Long productId, UploadProductDto uploadproductDto);
    Product setProductSold(Long productId);
    boolean deleteProduct(String username, Long productId);
}
