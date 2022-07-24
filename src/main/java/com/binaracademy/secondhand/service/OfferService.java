package com.binaracademy.secondhand.service;

import com.binaracademy.secondhand.dto.OfferUploadDto;
import com.binaracademy.secondhand.model.Offer;
import java.util.List;

public interface OfferService {
    Offer saveOffer(String email, OfferUploadDto offer);
    List<Offer> getBuyerOffers(String email);
    List<Offer> getSellerOffers(String email);
    Offer getOffer(Long offerId);
    Boolean acceptOffer(Long offerId);
    Boolean declineOffer(Long offerId);
    Boolean completeOffer(Long offerId);
    Boolean cancelOffer(Long offerId);
}
