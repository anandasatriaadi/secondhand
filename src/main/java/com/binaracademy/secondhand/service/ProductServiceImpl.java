package com.binaracademy.secondhand.service;

import com.binaracademy.secondhand.dto.ProductUploadDto;
import com.binaracademy.secondhand.model.Category;
import com.binaracademy.secondhand.model.Product;
import com.binaracademy.secondhand.model.ProductImage;
import com.binaracademy.secondhand.model.ProductOffer;
import com.binaracademy.secondhand.repository.CategoryRepository;
import com.binaracademy.secondhand.repository.ProductImageRepository;
import com.binaracademy.secondhand.repository.ProductOfferRepository;
import com.binaracademy.secondhand.repository.ProductRepository;
import com.binaracademy.secondhand.repository.UserRepository;
import com.binaracademy.secondhand.util.enums.ProductStatus;
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
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductOfferRepository productOfferRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductImageService productImageService;

    // ========================================================================
    //   START ::: CREATE PRODUCT SECTION
    // ========================================================================

    // ======== Create/save product ========
    @Override
    public Product saveProduct(String email, ProductUploadDto uploadProductDto) throws ResponseStatusException {
        checkProductImage(uploadProductDto);

        // ======== Check if category exists ========
        Optional<Category> categoryExist = categoryRepository.findById(uploadProductDto.getCategoryId());
        if (!categoryExist.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category not found");
        }

        try {
            if (userPublishedCount(email) >= 4) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User has max 4 products");
            }
            checkProductDto(uploadProductDto);

            Long userId = userRepository.findByEmail(email).getId();

            // ======== Assign DTO to Model ========
            Product product = new Product();
            product.setName(uploadProductDto.getName());
            product.setDescription(uploadProductDto.getDescription());
            product.setPrice(uploadProductDto.getPrice());
            product.setAddress(uploadProductDto.getAddress());
            product.setProductStatus(ProductStatus.PUBLISHED);
            product.setUserId(userId);
            product.setCategoryId(categoryExist.get().getId());
            product.setCategory(categoryExist.get());
            Product productDb = productRepository.save(product);

            productImageService.saveProductImages(productDb.getId(), uploadProductDto.getImages());

            return productDb;
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getReason());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    // ========================================================================
    //   END ::: CREATE PRODUCT SECTION
    // ========================================================================

    // ========================================================================
    //   START ::: READ PRODUCT SECTION
    // ========================================================================

    // ======== Get product detail ========
    @Override
    public List<Product> getSellerProducts(String email) {
        Long userId = userRepository.findByEmail(email).getId();

        return productRepository.findByUserId(userId);
    }

    // ======== Get product detail ========
    @Override
    public Product getProduct(Long id) {
        Optional<Product> productExist = productRepository.findById(id);
        if (productExist.isPresent()) {
            return productExist.get();
        } else {
            return null;
        }
    }

    // ======== Get products with search and pagination ========
    @Override
    public List<Product> getAllProducts(String search, int page, int size) {
        if (search.equals("")) {
            return productRepository.findByProductStatus(ProductStatus.PUBLISHED, PageRequest.of(page, size));
        }
        log.info("Searching for product: " + search);
        return productRepository.findAllAndSearch(search, PageRequest.of(page, size));
    }

    // ======== Get products by category ========
    @Override
    public List<Product> getProductsByCategory(Long categoryId, int page, int size) {
        return productRepository.findByCategory(categoryId, PageRequest.of(page, size));
    }

    // ======== Get product images ========
    @Override
    public List<ProductImage> getAllProductImages(Long productId) {
        return productImageRepository.findByProductId(productId);
    }

    // ======== Get all product offers ========
    @Override
    public List<ProductOffer> getAllProductOffers(Long productId) {
        return productOfferRepository.findByProductId(productId);
    }

    // ========================================================================
    //   END ::: READ PRODUCT SECTION
    // ========================================================================

    // ========================================================================
    //   START ::: UPDATE PRODUCT SECTION
    // ========================================================================

    // ======== Update product ========
    @Override
    public Product updateProduct(String email, Long productId, ProductUploadDto uploadProductDto) {
        // ======== Check Repository ========
        Optional<Product> res = productRepository.findById(productId);
        if (!res.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found");
        }

        Long userId = userRepository.findByEmail(email).getId();
        if (!res.get().getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not authorized to update this product");
        }

        checkProductImage(uploadProductDto);

        // ======== Check if category exists ========
        Optional<Category> categoryExist = categoryRepository.findById(uploadProductDto.getCategoryId());
        if (!categoryExist.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category not found");
        }

        try {
            checkProductDto(uploadProductDto);

            // ======== Assign DTO to Model ========
            Product product = new Product();
            product.setId(productId);
            product.setName(uploadProductDto.getName());
            product.setDescription(uploadProductDto.getDescription());
            product.setPrice(uploadProductDto.getPrice());
            product.setAddress(uploadProductDto.getAddress());
            product.setProductStatus(ProductStatus.PUBLISHED);
            product.setUserId(userId);
            product.setCategoryId(categoryExist.get().getId());
            product.setCategory(categoryExist.get());
            Product productDb = productRepository.save(product);

            if (uploadProductDto.getImages().length > 0) {
                List<ProductImage> oldImages = productImageRepository.findByProductId(productId);
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

    // ======== Update product status to sold ========
    @Override
    public Product setProductSold(Long productId) {
        Optional<Product> result = productRepository.findById(productId);
        // ======== Check Repository ========
        if (!result.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found");
        }

        Product product = result.get();
        product.setProductStatus(ProductStatus.SOLD);
        return productRepository.save(product);
    }

    // ========================================================================
    //   END ::: UPDATE PRODUCT SECTION
    // ========================================================================

    // ========================================================================
    //   START ::: DELETE PRODUCT SECTION
    // ========================================================================

    // ======== Delete product ========
    @Override
    public boolean deleteProduct(String email, Long productId) {
        Optional<Product> res = productRepository.findById(productId);
        Product product = null;
        if (res.isPresent()) {
            product = res.get();
            if (userRepository.findByEmail(email).getId().equals(product.getUserId())) {
                List<ProductImage> oldImages = productImageRepository.findByProductId(productId);
                productImageService.deleteProductImages(oldImages);
                productRepository.deleteById(productId);
                return true;
            }
        }
        return false;
    }

    // ========================================================================
    //   END ::: DELETE PRODUCT SECTION
    // ========================================================================

    // ========================================================================
    //   HELPER FUNCTIONS
    // ========================================================================

    // ======== Check product dto ========
    private void checkProductDto(ProductUploadDto uploadProductDto) throws IllegalArgumentException {
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
            msg += uploadProductDto.getAddress() == null ? "address, " : "";
            msg += "can't be null";
            throw new IllegalArgumentException(msg);
        }
    }

    private void checkProductImage(ProductUploadDto uploadProductDto) {
        // ======== Check Images Count ========
        if (uploadProductDto.getImages().length > 4 || uploadProductDto.getImages().length == 1) {
            if (uploadProductDto.getImages().length > 4) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Max 4 images");
            }
        }
    }

    // ======== Check user published product count ========
    private long userPublishedCount(String email) {
        Long userId = userRepository.findByEmail(email).getId();
        long res = productRepository.countByUserIdAndProductStatus(userId, ProductStatus.PUBLISHED);
        log.info(res + " products published by user " + email);
        return res;
    }
}
