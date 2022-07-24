package com.binaracademy.secondhand.service;

import com.binaracademy.secondhand.dto.OfferUploadDto;
import com.binaracademy.secondhand.model.Offer;
import com.binaracademy.secondhand.model.Product;
import com.binaracademy.secondhand.model.UserTransaction;
import com.binaracademy.secondhand.repository.OfferRepository;
import com.binaracademy.secondhand.repository.ProductRepository;
import com.binaracademy.secondhand.repository.UserRepository;
import com.binaracademy.secondhand.repository.UserTransactionRepository;
import com.binaracademy.secondhand.util.enums.OfferStatus;
import com.binaracademy.secondhand.util.enums.ProductStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class OfferServiceImpl implements OfferService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ProductRepository productRepository;

    @Autowired
    private final OfferRepository productOfferRepository;

    @Autowired
    private final UserTransactionRepository userTransactionRepository;

    @Override
    public Offer saveOffer(String email, OfferUploadDto offer) {
        Optional<Product> productResult = productRepository.findById(offer.getProductId());
        if (productResult.isPresent()) {
            Long userId = userRepository.findByEmail(email).getId();

            if (userId.equals(productResult.get().getUserId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User cannot offer their own product");
            }

            Offer productOffer = new Offer();
            productOffer.setProductId(offer.getProductId());
            productOffer.setBuyerId(userId);
            productOffer.setSellerId(productResult.get().getUserId());
            productOffer.setOfferStatus(OfferStatus.PENDING);
            productOffer.setOfferPrice(offer.getOfferPrice());
            productOffer.setCreatedAt(LocalDateTime.now());

            return productOfferRepository.save(productOffer);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
    }

    @Override
    public List<Offer> getBuyerOffers(String email) {
        return productOfferRepository.findByBuyerId(userRepository.findByEmail(email).getId());
    }

    @Override
    public List<Offer> getSellerOffers(String email) {
        return productOfferRepository.findBySellerId(userRepository.findByEmail(email).getId());
    }

    @Override
    public Offer getOffer(Long offerId) {
        Optional<Offer> productOffer = productOfferRepository.findById(offerId);
        return productOffer.isPresent() ? productOffer.get() : null;
    }

    @Override
    public Boolean acceptOffer(Long offerId) {
        return setOfferStatus(offerId, OfferStatus.ACCEPTED);
    }

    @Override
    public Boolean declineOffer(Long offerId) {
        return setOfferStatus(offerId, OfferStatus.DECLINED);
    }

    @Override
    public Boolean completeOffer(Long offerId) {
        Optional<Offer> queryResult = productOfferRepository.findById(offerId);

        if (queryResult.isPresent()) {
            UserTransaction userTransaction = new UserTransaction();
            userTransaction.setProductId(queryResult.get().getProductId());
            userTransaction.setBuyerId(queryResult.get().getBuyerId());
            userTransaction.setSellerId(queryResult.get().getSellerId());
            userTransaction.setPrice(queryResult.get().getOfferPrice());
            userTransaction.setCreatedAt(LocalDateTime.now());

            userTransactionRepository.save(userTransaction);

            Optional<Product> soldProduct = productRepository.findById(queryResult.get().getProductId());

            if (soldProduct.isPresent()) {
                soldProduct.get().setProductStatus(ProductStatus.SOLD);
                productRepository.save(soldProduct.get());
            }

            return setOfferStatus(offerId, OfferStatus.COMPLETED);
        }
        return false;
    }

    @Override
    public Boolean cancelOffer(Long offerId) {
        return setOfferStatus(offerId, OfferStatus.CANCELLED);
    }

    // ========================================================================
    //   Utility methods
    // ========================================================================
    private Boolean setOfferStatus(Long offerId, OfferStatus status) {
        Optional<Offer> queryResult = productOfferRepository.findById(offerId);
        if (queryResult.isPresent()) {
            Offer offer = queryResult.get();
            offer.setOfferStatus(status);
            return productOfferRepository.save(offer) != null;
        }
        return false;
    }
}
