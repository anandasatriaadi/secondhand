package com.binaracademy.secondhand.service;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.binaracademy.secondhand.SecondhandApplication;
import com.binaracademy.secondhand.model.ProductImage;
import com.binaracademy.secondhand.repository.ProductImageRepository;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductImageServiceImpl implements ProductImageService{
    @Autowired
    private ProductImageRepository productImageRepository;
    
    @Override
    public boolean saveProductImages(Long productId, MultipartFile[] images) {
        for (int i = 0; i < images.length; i++) {
            try {
                File convertFile = new File(System.getProperty("java.io.tmpdir") + "/" + images[i].getOriginalFilename());
                images[i].transferTo(convertFile);
                String imageUrl = (String) SecondhandApplication.cloudinary.uploader().upload(convertFile, ObjectUtils.emptyMap()).get("url");
                
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

    @Override
    public boolean deleteProductImages(List<ProductImage> images) {
        for (ProductImage image : images) {
            try {
                String publicId = Paths.get(new URI(image.getImageUrl()).getPath()).getFileName().toString();
                publicId = publicId.substring(0, publicId.lastIndexOf("."));

                SecondhandApplication.cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap()).toString();

                productImageRepository.delete(image);
            } catch (Exception e) {
                log.error(e.getMessage());
                return false;
            }
        }
        return true;
    }
    
}