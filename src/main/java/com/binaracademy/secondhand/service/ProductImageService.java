package com.binaracademy.secondhand.service;

import com.binaracademy.secondhand.model.ProductImage;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ProductImageService {
    List<ProductImage> getProductImageList(Long productId);
    ProductImage getProductImage(Long productId);
    boolean saveProductImages(Long productId, MultipartFile[] images);
    boolean deleteProductImages(List<ProductImage> images);
}
