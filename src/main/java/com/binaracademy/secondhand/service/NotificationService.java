package com.binaracademy.secondhand.service;

import com.binaracademy.secondhand.model.Notification;
import java.util.List;

public interface NotificationService {
    Boolean addNotification(Notification notification);
    List<Notification> getNotification(String email);
    Boolean deleteNotification(Long notificationId);
}
