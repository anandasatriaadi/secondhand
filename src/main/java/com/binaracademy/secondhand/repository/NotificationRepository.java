package com.binaracademy.secondhand.repository;

import com.binaracademy.secondhand.model.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n WHERE n.userId = ?1")
    List<Notification> findNotificationByUserId(Long userId);
}
