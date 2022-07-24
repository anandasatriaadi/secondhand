package com.binaracademy.secondhand.controller;

import com.binaracademy.secondhand.dto.OfferUploadDto;
import com.binaracademy.secondhand.dto.RestResponseDto;
import com.binaracademy.secondhand.model.Notification;
import com.binaracademy.secondhand.model.Offer;
import com.binaracademy.secondhand.model.Product;
import com.binaracademy.secondhand.service.NotificationService;
import com.binaracademy.secondhand.service.OfferService;
import com.binaracademy.secondhand.service.ProductService;
import com.binaracademy.secondhand.service.UserService;
import com.binaracademy.secondhand.util.enums.NotificationType;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class OfferController {

    private final String OK_MSG = "ok";
    private final String FAILED_MSG = "failed";
    private final String FORBIDDEN_MSG = "This product does not belong to this user";

    @Autowired
    private final NotificationService notificationService;

    @Autowired
    private final UserService userService;

    @Autowired
    private final ProductService productService;

    @Autowired
    private final OfferService offerService;

    @PostMapping("/offer/save")
    public ResponseEntity<RestResponseDto> saveOffer(Authentication authentication, @RequestBody OfferUploadDto offer) {
        try {
            Offer result = offerService.saveOffer(authentication.getPrincipal().toString(), offer);

            Notification offerNotif = new Notification();
            offerNotif.setUserId(result.getSellerId());
            offerNotif.setOfferId(result.getId());
            offerNotif.setType(NotificationType.OFFER);
            offerNotif.setCreatedAt(LocalDateTime.now());

            notificationService.addNotification(offerNotif);

            return ResponseEntity.ok().body(new RestResponseDto(200, OK_MSG, result));
        } catch (ResponseStatusException e) {
            log.error("Error while saving offer");
            return ResponseEntity.status(e.getStatus()).body(new RestResponseDto(e.getRawStatusCode(), e.getReason(), ""));
        } catch (Exception e) {
            log.error("Error while saving offer");
            return ResponseEntity.badRequest().body(new RestResponseDto(400, FAILED_MSG, null));
        }
    }

    @GetMapping("/offers/buyer")
    public ResponseEntity<RestResponseDto> getBuyerOffers(Authentication authentication) {
        return ResponseEntity.ok(new RestResponseDto(200, OK_MSG, offerService.getBuyerOffers(authentication.getPrincipal().toString())));
    }

    @GetMapping("/offers/seller")
    public ResponseEntity<RestResponseDto> getSellerOffers(Authentication authentication) {
        return ResponseEntity.ok(new RestResponseDto(200, OK_MSG, offerService.getSellerOffers(authentication.getPrincipal().toString())));
    }

    @GetMapping("/offer/{offerId}/accept")
    public ResponseEntity<RestResponseDto> acceptOffer(Authentication authentication, @PathVariable Long offerId) {
        if (checkIfOfferValidForUser(authentication, offerId)) {
            if (offerService.acceptOffer(offerId).booleanValue()) {
                return ResponseEntity.ok(new RestResponseDto(200, OK_MSG, null));
            } else {
                log.error("Error while accepting offer {}", offerId);
                return ResponseEntity.badRequest().body(new RestResponseDto(400, FAILED_MSG, null));
            }
        }
        return ResponseEntity.status(403).body(new RestResponseDto(403, FORBIDDEN_MSG, null));
    }

    @GetMapping("/offer/{offerId}/decline")
    public ResponseEntity<RestResponseDto> declineOffer(Authentication authentication, @PathVariable Long offerId) {
        if (checkIfOfferValidForUser(authentication, offerId)) {
            if (offerService.declineOffer(offerId).booleanValue()) {
                return ResponseEntity.ok(new RestResponseDto(200, OK_MSG, null));
            } else {
                log.error("Error while declining offer {}", offerId);
                return ResponseEntity.badRequest().body(new RestResponseDto(400, FAILED_MSG, null));
            }
        }
        return ResponseEntity.status(403).body(new RestResponseDto(403, FORBIDDEN_MSG, null));
    }

    @GetMapping("/offer/{offerId}/complete")
    public ResponseEntity<RestResponseDto> completeOffer(Authentication authentication, @PathVariable Long offerId) {
        if (checkIfOfferValidForUser(authentication, offerId)) {
            if (offerService.completeOffer(offerId).booleanValue()) {
                return ResponseEntity.ok(new RestResponseDto(200, OK_MSG, null));
            } else {
                log.error("Error while completing offer {}", offerId);
                return ResponseEntity.badRequest().body(new RestResponseDto(400, FAILED_MSG, null));
            }
        }
        return ResponseEntity.status(403).body(new RestResponseDto(403, FORBIDDEN_MSG, null));
    }

    @GetMapping("/offer/{offerId}/cancel")
    public ResponseEntity<RestResponseDto> cancelOffer(Authentication authentication, @PathVariable Long offerId) {
        if (checkIfOfferValidForUser(authentication, offerId)) {
            if (offerService.cancelOffer(offerId).booleanValue()) {
                return ResponseEntity.ok(new RestResponseDto(200, OK_MSG, null));
            } else {
                log.error("Error while cancelling offer {}", offerId);
                return ResponseEntity.badRequest().body(new RestResponseDto(400, FAILED_MSG, null));
            }
        }
        return ResponseEntity.status(403).body(new RestResponseDto(403, FORBIDDEN_MSG, null));
    }

    // ========================================================================
    //   UTILITY FUNCTIONS
    // ========================================================================
    private boolean checkIfOfferValidForUser(Authentication authentication, Long offerId) {
        Product result = productService.getProduct(offerService.getOffer(offerId).getProductId());

        return result.getUserId().equals(userService.getUser(authentication.getPrincipal().toString()).getId());
    }
}
