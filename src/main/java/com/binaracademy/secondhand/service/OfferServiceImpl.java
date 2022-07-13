package com.binaracademy.secondhand.service;

import com.binaracademy.secondhand.dto.ProductOfferUploadDto;
import com.binaracademy.secondhand.model.ProductOffer;
import com.binaracademy.secondhand.repository.ProductOfferRepository;
import com.binaracademy.secondhand.repository.UserRepository;
import com.binaracademy.secondhand.util.enums.OfferStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OfferServiceImpl implements OfferService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ProductOfferRepository productOfferRepository;

    @Autowired
    private final ModelMapper modelMapper;

    @Override
    public ProductOffer saveOffer(String email, ProductOfferUploadDto offer) {
        Long userId = userRepository.findByEmail(email).getId();
        ProductOffer productOffer = modelMapper.map(offer, ProductOffer.class);
        productOffer.setUserId(userId);
        productOffer.setOfferStatus(OfferStatus.PENDING);
        productOffer.setCreatedAt(LocalDateTime.now());

        return productOfferRepository.save(productOffer);
    }

    @Override
    public List<ProductOffer> getOffers(String email) {
        return productOfferRepository.findOfferByUserId(userRepository.findByEmail(email).getId());
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
        return setOfferStatus(offerId, OfferStatus.COMPLETED);
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
