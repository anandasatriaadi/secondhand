package com.binaracademy.secondhand.controller;

import com.binaracademy.secondhand.dto.ProductOfferUploadDto;
import com.binaracademy.secondhand.dto.RestResponseDto;
import com.binaracademy.secondhand.model.Notification;
import com.binaracademy.secondhand.model.ProductOffer;
import com.binaracademy.secondhand.service.NotificationService;
import com.binaracademy.secondhand.service.OfferService;
import com.binaracademy.secondhand.util.enums.NotificationType;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class OfferController {

    private final String OK_MSG = "ok";
    private final String FAILED_MSG = "failed";

    @Autowired
    private final NotificationService notificationService;

    @Autowired
    private final OfferService offerService;

    @PostMapping("/offer/save")
    public RestResponseDto saveOffer(Authentication authentication, @RequestBody ProductOfferUploadDto offer) {
        try {
            ProductOffer result = offerService.saveOffer(authentication.getPrincipal().toString(), offer);

            Notification offerNotif = new Notification();
            offerNotif.setUserId(result.getUserId());
            offerNotif.setOfferId(result.getId());
            offerNotif.setType(NotificationType.OFFER);
            offerNotif.setCreatedAt(LocalDateTime.now());

            notificationService.addNotification(offerNotif);

            return new RestResponseDto(200, OK_MSG, result);
        } catch (Exception e) {
            log.error("Error while saving offer", e);
            return new RestResponseDto(400, FAILED_MSG, null);
        }
    }

    @GetMapping("/offers")
    public RestResponseDto getOffer(Authentication authentication) {
        return new RestResponseDto(200, OK_MSG, offerService.getOffers(authentication.getPrincipal().toString()));
    }

    @PostMapping("/offer/accept/{id}")
    public RestResponseDto acceptOffer(@RequestBody Long id) {
        if (offerService.acceptOffer(id).booleanValue()) {
            return new RestResponseDto(200, OK_MSG, null);
        } else {
            log.error("Error while accepting offer {}", id);
            return new RestResponseDto(400, FAILED_MSG, null);
        }
    }

    @PostMapping("/offer/decline/{id}")
    public RestResponseDto declineOffer(@RequestBody Long id) {
        if (offerService.declineOffer(id).booleanValue()) {
            return new RestResponseDto(200, OK_MSG, null);
        } else {
            log.error("Error while declining offer {}", id);
            return new RestResponseDto(400, FAILED_MSG, null);
        }
    }

    @PostMapping("/offer/complete/{id}")
    public RestResponseDto completeOffer(@RequestBody Long id) {
        if (offerService.completeOffer(id).booleanValue()) {
            return new RestResponseDto(200, OK_MSG, null);
        } else {
            log.error("Error while completing offer {}", id);
            return new RestResponseDto(400, FAILED_MSG, null);
        }
    }

    @PostMapping("/offer/cancel/{id}")
    public RestResponseDto cancelOffer(@RequestBody Long id) {
        if (offerService.cancelOffer(id).booleanValue()) {
            return new RestResponseDto(200, OK_MSG, null);
        } else {
            log.error("Error while cancelling offer {}", id);
            return new RestResponseDto(400, FAILED_MSG, null);
        }
    }
}
