package com.binaracademy.secondhand.service;

import com.binaracademy.secondhand.model.Notification;
import com.binaracademy.secondhand.repository.NotificationRepository;
import com.binaracademy.secondhand.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Boolean addNotification(Notification notification) {
        try {
            notificationRepository.save(notification);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public List<Notification> getNotification(String email) {
        Long userId = userRepository.findByEmail(email).getId();
        return notificationRepository.findByUserId(userId);
    }

    @Override
    public Boolean deleteNotification(Long notificationId) {
        try {
            notificationRepository.deleteById(notificationId);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }
}
