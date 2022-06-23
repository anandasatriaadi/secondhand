package com.binaracademy.secondhand.service;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.binaracademy.secondhand.SecondhandApplication;
import com.binaracademy.secondhand.dto.UploadProductDto;
import com.binaracademy.secondhand.model.Category;
import com.binaracademy.secondhand.model.Product;
import com.binaracademy.secondhand.model.ProductImage;
import com.binaracademy.secondhand.model.ProductOffer;
import com.binaracademy.secondhand.repository.CategoryRepository;
import com.binaracademy.secondhand.repository.ProductImageRepository;
import com.binaracademy.secondhand.repository.ProductRepository;
import com.binaracademy.secondhand.repository.UserRepository;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Product saveProduct(String username, UploadProductDto uploadProductDto) {
        // ======== Check Images Count ========
        if(uploadProductDto.getImages().length > 4) {
            throw new IllegalArgumentException("Max 4 images");
        }
        
        // ======== Check if category exists ========
        Optional<Category> categoryExist = categoryRepository.findById(uploadProductDto.getCategoryId());
        if(!categoryExist.isPresent()) {
            throw new IllegalArgumentException("Category not found");
        }

        
        // ======== Check if there are any nulls ========
        if(uploadProductDto.getName() != null && uploadProductDto.getDescription() != null && uploadProductDto.getCategoryId() != null &&
            uploadProductDto.getPrice() != null && uploadProductDto.getAddress() != null) {
            
            Long userId = userRepository.findByUsername(username).getId();
            
            // ======== Assign DTO to Model ========
            Product product = new Product();
            product.setName(uploadProductDto.getName());
            product.setDescription(uploadProductDto.getDescription());
            product.setPrice(uploadProductDto.getPrice());
            product.setAddress(uploadProductDto.getAddress());
            product.setUserId(userId);
            product.setCategoryId(categoryExist.get().getId());
            product.setCategory(categoryExist.get());
            Product productDb = productRepository.save(product);

            // ======== Upload Image and Add Image to ProductImage  ========
            MultipartFile[] images = uploadProductDto.getImages();
            for (int i = 0; i < images.length; i++) {
                try {
                    File convertFile = new File(System.getProperty("java.io.tmpdir") + "/" + images[i].getOriginalFilename());
                    images[i].transferTo(convertFile);
                    String imageUrl = (String) SecondhandApplication.cloudinary.uploader().upload(convertFile, ObjectUtils.emptyMap()).get("url");
                    
                    ProductImage productImage = new ProductImage();
                    productImage.setImageUrl(imageUrl);
                    productImage.setProductId(productDb.getId());
                    productImageRepository.save(productImage);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }

            return productDb;
        } else {
            String msg = "Product ";
            msg += uploadProductDto.getName() == null ? "name, " : "";
            msg += uploadProductDto.getDescription() == null ? "description, " : "";
            msg += uploadProductDto.getCategoryId() == null ? "category, " : "";
            msg += uploadProductDto.getPrice() == null ? "price, " : "";
            msg += uploadProductDto.getAddress() == null ? "address " : "";
            msg += "can't be null";
            throw new IllegalArgumentException(msg);
        }
    }

    @Override
    public Product updateProduct(String username, Long id, UploadProductDto uploadProductDto) {
        // ======== Check Repository ========
        if(!productRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("Product not found");
        }

        // ======== Check Images Count ========
        if(uploadProductDto.getImages().length > 4) {
            throw new IllegalArgumentException("Max 4 images");
        }
        
        // ======== Check if category exists ========
        Optional<Category> categoryExist = categoryRepository.findById(uploadProductDto.getCategoryId());
        if(!categoryExist.isPresent()) {
            throw new IllegalArgumentException("Category not found");
        }
        
        // ======== Check if there are any nulls ========
        if(uploadProductDto.getName() != null && uploadProductDto.getDescription() != null && uploadProductDto.getCategoryId() != null &&
            uploadProductDto.getPrice() != null && uploadProductDto.getAddress() != null) {
            
            Long userId = userRepository.findByUsername(username).getId();
            
            // ======== Assign DTO to Model ========
            Product product = new Product();
            product.setId(id);
            product.setName(uploadProductDto.getName());
            product.setDescription(uploadProductDto.getDescription());
            product.setPrice(uploadProductDto.getPrice());
            product.setAddress(uploadProductDto.getAddress());
            product.setUserId(userId);
            product.setCategoryId(categoryExist.get().getId());
            product.setCategory(categoryExist.get());
            Product productDb = productRepository.save(product);

            // ======== Delete old images before upload ========
            List<ProductImage> oldImages = productRepository.findAllProductImages(id);
            for (ProductImage oldImage : oldImages) {
                try {
                    String publicId = Paths.get(new URI(oldImage.getImageUrl()).getPath()).getFileName().toString();
                    publicId = publicId.substring(0, publicId.lastIndexOf("."));

                    SecondhandApplication.cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap()).toString();

                    productImageRepository.delete(oldImage);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }

            // ======== Upload Image and Add Image to ProductImage  ========
            MultipartFile[] images = uploadProductDto.getImages();
            for (int i = 0; i < images.length; i++) {
                try {
                    File convertFile = new File(System.getProperty("java.io.tmpdir") + "/" + images[i].getOriginalFilename());
                    images[i].transferTo(convertFile);
                    String imageUrl = (String) SecondhandApplication.cloudinary.uploader().upload(convertFile, ObjectUtils.emptyMap()).get("url");
                    
                    ProductImage productImage = new ProductImage();
                    productImage.setImageUrl(imageUrl);
                    productImage.setProductId(productDb.getId());
                    productImageRepository.save(productImage);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }

            return productDb;
        } else {
            String msg = "Product ";
            msg += uploadProductDto.getName() == null ? "name, " : "";
            msg += uploadProductDto.getDescription() == null ? "description, " : "";
            msg += uploadProductDto.getCategoryId() == null ? "category, " : "";
            msg += uploadProductDto.getPrice() == null ? "price, " : "";
            msg += uploadProductDto.getAddress() == null ? "address " : "";
            msg += "can't be null";
            throw new IllegalArgumentException(msg);
        }
    }

    @Override
    public Product getProduct(Long id) {
        Optional<Product> productExist = productRepository.findById(id);
        if(productExist.isPresent()) {
            return productExist.get();
        } else {
            return null;
        }
    }

    @Override
    public List<ProductOffer> getAllProductOffers(Long productId) {
        return productRepository.findAllProductOffers(productId);
    }

    @Override
    public List<ProductImage> getAllProductImages(Long productId) {
        return productRepository.findAllProductImages(productId);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
}
