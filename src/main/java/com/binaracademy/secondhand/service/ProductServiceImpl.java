package com.binaracademy.secondhand.service;

import com.binaracademy.secondhand.dto.UploadProductDto;
import com.binaracademy.secondhand.model.Category;
import com.binaracademy.secondhand.model.Product;
import com.binaracademy.secondhand.model.ProductImage;
import com.binaracademy.secondhand.model.ProductOffer;
import com.binaracademy.secondhand.repository.CategoryRepository;
import com.binaracademy.secondhand.repository.ProductRepository;
import com.binaracademy.secondhand.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
        if (uploadProductDto.getImages().length > 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Max 4 images");
        }

        // ======== Check if category exists ========
        Optional<Category> categoryExist = categoryRepository.findById(uploadProductDto.getCategoryId());
        if (!categoryExist.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category not found");
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
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    @Override
    public Product updateProduct(String username, Long id, UploadProductDto uploadProductDto) {
        // ======== Check Repository ========
        if (!productRepository.findById(id).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found");
        }

        // ======== Check Images Count ========
        if (uploadProductDto.getImages().length > 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Max 4 images");
        }

        // ======== Check if category exists ========
        Optional<Category> categoryExist = categoryRepository.findById(uploadProductDto.getCategoryId());
        if (!categoryExist.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category not found");
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

            if (uploadProductDto.getImages().length > 0) {
                List<ProductImage> oldImages = productRepository.findAllProductImages(id);
                productImageService.deleteProductImages(oldImages);

                productImageService.saveProductImages(productDb.getId(), uploadProductDto.getImages());
            }

            return productDb;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    @Override
    public Product getProduct(Long id) {
        Optional<Product> productExist = productRepository.findById(id);
        if (productExist.isPresent()) {
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
    public List<Product> getAllProducts(String search, int page, int size) {
        if (search.equals("")) {
            return productRepository.findAll(PageRequest.of(page, size)).getContent();
        }
        log.info("Searching for product: " + search);
        return productRepository.findAllAndSearch(search, PageRequest.of(page, size));
    }

    @Override
    public List<Product> getProductsByCategory(Long categoryId, int page, int size) {
        return productRepository.findProductsByCategory(categoryId, PageRequest.of(page, size));
    }

    @Override
    public boolean deleteProduct(String username, Long productId) {
        Optional<Product> res = productRepository.findById(productId);
        Product product = null;
        if (res.isPresent()) {
            product = res.get();
            if (userRepository.findByUsername(username).getId() == product.getUserId()) {
                List<ProductImage> oldImages = productRepository.findAllProductImages(productId);
                productImageService.deleteProductImages(oldImages);
                productRepository.deleteById(productId);
                return true;
            }
        }
        return false;
    }

    // ========================================================================
    //   HELPER FUNCTIONS
    // ========================================================================
    private void checkProductDto(UploadProductDto uploadProductDto) throws IllegalArgumentException {
        if (
            uploadProductDto.getName() == null ||
            uploadProductDto.getDescription() == null ||
            uploadProductDto.getCategoryId() == null ||
            uploadProductDto.getPrice() == null ||
            uploadProductDto.getAddress() == null
        ) {
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
