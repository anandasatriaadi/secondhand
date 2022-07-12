package com.binaracademy.secondhand.service;

import com.binaracademy.secondhand.model.ProductOffer;

public interface OfferService {
    ProductOffer saveOffer(ProductOffer offer);
    Boolean acceptOffer(Long offerId);
    Boolean declineOffer(Long offerId);
}
