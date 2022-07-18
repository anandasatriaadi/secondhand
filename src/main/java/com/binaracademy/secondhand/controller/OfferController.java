package com.binaracademy.secondhand.controller;

import com.binaracademy.secondhand.dto.ProductOfferUploadDto;
import com.binaracademy.secondhand.dto.RestResponseDto;
import com.binaracademy.secondhand.model.Notification;
import com.binaracademy.secondhand.model.ProductOffer;
import com.binaracademy.secondhand.service.NotificationService;
import com.binaracademy.secondhand.service.OfferService;
import com.binaracademy.secondhand.util.enums.NotificationType;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
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

    @Autowired
    private final NotificationService notificationService;

    @Autowired
    private final OfferService offerService;

    @PostMapping("/offer/save")
    public ResponseEntity<RestResponseDto> saveOffer(Authentication authentication, @RequestBody ProductOfferUploadDto offer) {
        try {
            ProductOffer result = offerService.saveOffer(authentication.getPrincipal().toString(), offer);

            Notification offerNotif = new Notification();
            offerNotif.setUserId(result.getUserId());
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

    @GetMapping("/offers")
    public ResponseEntity<RestResponseDto> getOffer(Authentication authentication) {
        return ResponseEntity.ok(new RestResponseDto(200, OK_MSG, offerService.getOffers(authentication.getPrincipal().toString())));
    }

    @PostMapping("/offer/accept")
    public ResponseEntity<RestResponseDto> acceptOffer(@RequestBody Map<?, ?> requestMap) {
        if (offerService.acceptOffer(((Integer) requestMap.get("id")).longValue()).booleanValue()) {
            return ResponseEntity.ok(new RestResponseDto(200, OK_MSG, null));
        } else {
            log.error("Error while accepting offer {}", ((Integer) requestMap.get("id")).longValue());
            return ResponseEntity.badRequest().body(new RestResponseDto(400, FAILED_MSG, null));
        }
    }

    @PostMapping("/offer/decline")
    public ResponseEntity<RestResponseDto> declineOffer(@RequestBody Map<?, ?> requestMap) {
        if (offerService.declineOffer(((Integer) requestMap.get("id")).longValue()).booleanValue()) {
            return ResponseEntity.ok(new RestResponseDto(200, OK_MSG, null));
        } else {
            log.error("Error while declining offer {}", ((Integer) requestMap.get("id")).longValue());
            return ResponseEntity.badRequest().body(new RestResponseDto(400, FAILED_MSG, null));
        }
    }

    @PostMapping("/offer/complete")
    public ResponseEntity<RestResponseDto> completeOffer(@RequestBody Map<?, ?> requestMap) {
        if (offerService.completeOffer(((Integer) requestMap.get("id")).longValue()).booleanValue()) {
            return ResponseEntity.ok(new RestResponseDto(200, OK_MSG, null));
        } else {
            log.error("Error while completing offer {}", ((Integer) requestMap.get("id")).longValue());
            return ResponseEntity.badRequest().body(new RestResponseDto(400, FAILED_MSG, null));
        }
    }

    @PostMapping("/offer/cancel")
    public ResponseEntity<RestResponseDto> cancelOffer(@RequestBody Map<?, ?> requestMap) {
        if (offerService.cancelOffer(((Integer) requestMap.get("id")).longValue()).booleanValue()) {
            return ResponseEntity.ok(new RestResponseDto(200, OK_MSG, null));
        } else {
            log.error("Error while cancelling offer {}", ((Integer) requestMap.get("id")).longValue());
            return ResponseEntity.badRequest().body(new RestResponseDto(400, FAILED_MSG, null));
        }
    }
}
