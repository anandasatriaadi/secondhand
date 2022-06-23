package com.binaracademy.secondhand.service;

import java.util.List;

import com.binaracademy.secondhand.dto.UploadProductDto;
import com.binaracademy.secondhand.model.Product;
import com.binaracademy.secondhand.model.ProductImage;
import com.binaracademy.secondhand.model.ProductOffer;

public interface ProductService {
	Product saveProduct(Long userId, UploadProductDto uploadproductDto);
	Product getProduct(Long id);
	List<ProductOffer> getAllProductOffers(Long productId);
	List<ProductImage> getAllProductImages(Long productId);
	List<Product> getAllProducts();
}
