package com.binaracademy.secondhand.service;

import com.binaracademy.secondhand.dto.ProductOfferUploadDto;
import com.binaracademy.secondhand.model.ProductOffer;
import java.util.List;

public interface OfferService {
    ProductOffer saveOffer(String email, ProductOfferUploadDto offer);
    List<ProductOffer> getBuyerOffers(String email);
    List<ProductOffer> getSellerOffers(String email);
    ProductOffer getOffer(Long offerId);
    Boolean acceptOffer(Long offerId);
    Boolean declineOffer(Long offerId);
    Boolean completeOffer(Long offerId);
    Boolean cancelOffer(Long offerId);
}
