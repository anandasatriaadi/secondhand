package com.binaracademy.secondhand.service;

import com.binaracademy.secondhand.dto.ProductOfferUploadDto;
import com.binaracademy.secondhand.model.Product;
import com.binaracademy.secondhand.model.ProductOffer;
import com.binaracademy.secondhand.model.UserTransaction;
import com.binaracademy.secondhand.repository.ProductOfferRepository;
import com.binaracademy.secondhand.repository.ProductRepository;
import com.binaracademy.secondhand.repository.UserRepository;
import com.binaracademy.secondhand.repository.UserTransactionRepository;
import com.binaracademy.secondhand.util.enums.OfferStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
    private final ProductOfferRepository productOfferRepository;

    @Autowired
    private final UserTransactionRepository userTransactionRepository;

    @Autowired
    private final ModelMapper modelMapper;

    @Override
    public ProductOffer saveOffer(String email, ProductOfferUploadDto offer) {
        if (productRepository.findById(offer.getProductId()).isPresent()) {
            Long userId = userRepository.findByEmail(email).getId();
            ProductOffer productOffer = modelMapper.map(offer, ProductOffer.class);
            productOffer.setUserId(userId);
            productOffer.setOfferStatus(OfferStatus.PENDING);
            productOffer.setCreatedAt(LocalDateTime.now());

            return productOfferRepository.save(productOffer);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
    }

    @Override
    public List<ProductOffer> getOffers(String email) {
        return productOfferRepository.findByUserId(userRepository.findByEmail(email).getId());
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
        Optional<ProductOffer> queryResult = productOfferRepository.findById(offerId);

        if (queryResult.isPresent()) {
            Optional<Product> queryResult2 = productRepository.findById(queryResult.get().getProductId());

            if (queryResult2.isPresent()) {
                UserTransaction userTransaction = new UserTransaction();
                userTransaction.setProductId(queryResult.get().getProductId());
                userTransaction.setBuyerId(queryResult.get().getUserId());
                userTransaction.setSellerId(queryResult2.get().getUserId());
                userTransaction.setPrice(queryResult.get().getOfferPrice());
                userTransaction.setCreatedAt(LocalDateTime.now());

                userTransactionRepository.save(userTransaction);

                return setOfferStatus(offerId, OfferStatus.COMPLETED);
            }
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
        Optional<ProductOffer> queryResult = productOfferRepository.findById(offerId);
        if (queryResult.isPresent()) {
            ProductOffer offer = queryResult.get();
            offer.setOfferStatus(status);
            return productOfferRepository.save(offer) != null;
        }
        return false;
    }
}
