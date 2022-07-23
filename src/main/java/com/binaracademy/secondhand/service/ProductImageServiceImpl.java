package com.binaracademy.secondhand.service;

import com.binaracademy.secondhand.model.ProductImage;
import com.binaracademy.secondhand.repository.ProductImageRepository;
import com.binaracademy.secondhand.util.CloudinaryUtil;
import com.cloudinary.utils.ObjectUtils;
import java.io.File;
import java.net.URI;
import java.nio.file.Paths;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductImageServiceImpl implements ProductImageService {

    @Autowired
    private ProductImageRepository productImageRepository;

    // ========================================================================
    //   START ::: CREATE PRODUCT IMAGES SECTION
    // ========================================================================

    @Override
    public boolean saveProductImages(Long productId, MultipartFile[] images) {
        for (int i = 0; i < images.length; i++) {
            try {
                File convertFile = new File(System.getProperty("java.io.tmpdir") + "/" + images[i].getOriginalFilename());
                images[i].transferTo(convertFile);
                String imageUrl = (String) CloudinaryUtil.cloudinary.uploader().upload(convertFile, ObjectUtils.emptyMap()).get("url");

                ProductImage productImage = new ProductImage();
                productImage.setImageUrl(imageUrl);
                productImage.setProductId(productId);
                productImageRepository.save(productImage);
            } catch (Exception e) {
                log.error(e.getMessage());
                return false;
            }
        }
        return true;
    }

    // ========================================================================
    //   END ::: CREATE PRODUCT IMAGES SECTION
    // ========================================================================

    // ========================================================================
    //   START ::: READ PRODUCT IMAGES SECTION
    // ========================================================================

    @Override
    public List<ProductImage> getProductImageList(Long productId) {
        return productImageRepository.findByProductId(productId);
    }

    @Override
    public ProductImage getProductImage(Long productId) {
        return productImageRepository.findFirstByProductId(productId);
    }

    // ========================================================================
    //   END ::: READ PRODUCT IMAGES SECTION
    // ========================================================================

    // ========================================================================
    //   START ::: DELETE PRODUCT IMAGES SECTION
    // ========================================================================

    @Override
    public boolean deleteProductImages(List<ProductImage> images) {
        for (ProductImage image : images) {
            try {
                String publicId = Paths.get(new URI(image.getImageUrl()).getPath()).getFileName().toString();
                publicId = publicId.substring(0, publicId.lastIndexOf("."));

                CloudinaryUtil.cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap()).toString();

                productImageRepository.delete(image);
            } catch (Exception e) {
                log.error(e.getMessage());
                return false;
            }
        }
        return true;
    }
    // ========================================================================
    //   END ::: DELETE PRODUCT IMAGES SECTION
    // ========================================================================
}
