package com.binaracademy.secondhand.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.binaracademy.secondhand.model.ProductImage;

public interface ProductImageService {
    boolean saveProductImages(Long productId, MultipartFile[] images);
    boolean deleteProductImages(List<ProductImage> images);
}
