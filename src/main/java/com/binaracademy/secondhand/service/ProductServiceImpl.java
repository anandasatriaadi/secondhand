package com.binaracademy.secondhand.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.binaracademy.secondhand.dto.UploadProductDto;
import com.binaracademy.secondhand.model.Category;
import com.binaracademy.secondhand.model.Product;
import com.binaracademy.secondhand.model.ProductImage;
import com.binaracademy.secondhand.model.ProductOffer;
import com.binaracademy.secondhand.repository.CategoryRepository;
import com.binaracademy.secondhand.repository.ProductRepository;
import com.binaracademy.secondhand.repository.UserRepository;

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
    private UserRepository userRepository;

    @Autowired
    private ProductImageService productImageService;

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

        
        try {
            checkProductDto(uploadProductDto);
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
    
            productImageService.saveProductImages(productDb.getId(), uploadProductDto.getImages());

            return productDb;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
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
        
        try {
            checkProductDto(uploadProductDto);
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
    
            List<ProductImage> oldImages = productRepository.findAllProductImages(id);
            productImageService.deleteProductImages(oldImages);
    
            productImageService.saveProductImages(productDb.getId(), uploadProductDto.getImages());
    
            return productDb;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
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
    
    // ========================================================================
    //   HELPER FUNCTIONS
    // ========================================================================
    private void checkProductDto(UploadProductDto uploadProductDto) throws IllegalArgumentException{
        if(uploadProductDto.getName() == null|| uploadProductDto.getDescription() == null|| uploadProductDto.getCategoryId() == null||
            uploadProductDto.getPrice() == null|| uploadProductDto.getAddress() == null) {

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
}
