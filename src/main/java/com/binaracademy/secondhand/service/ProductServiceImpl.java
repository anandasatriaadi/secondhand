package com.binaracademy.secondhand.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.binaracademy.secondhand.dto.ProductDto;
import com.binaracademy.secondhand.model.Category;
import com.binaracademy.secondhand.model.Product;
import com.binaracademy.secondhand.model.ProductImage;
import com.binaracademy.secondhand.model.ProductOffer;
import com.binaracademy.secondhand.repository.CategoryRepository;
import com.binaracademy.secondhand.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Product saveProduct(ProductDto productDto) {
        // ======== Check if product images surpasses limit ========
        // if(productDto.getProductImages().size() > 4) {
        //     throw new IllegalArgumentException("Product can't have more than 4 images");
        // }

        if(productDto.getName() != null && productDto.getDescription() != null && productDto.getCategoryId() != null &&
                productDto.getPrice() != null && productDto.getAddress() != null) {

            Product product = new Product();
            product.setName(productDto.getName());
            product.setDescription(productDto.getDescription());
            product.setPrice(productDto.getPrice());
            product.setAddress(productDto.getAddress());

            Optional<Category> categoryExist = categoryRepository.findById(productDto.getCategoryId());
            if(categoryExist.isPresent()) {
                product.setCategory(categoryExist.get());
            }

            return productRepository.save(product);
        } else {
            return null;
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
