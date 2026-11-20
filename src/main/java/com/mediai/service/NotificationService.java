package com.mediai.service;

import com.mediai.entity.Notification;
import com.mediai.entity.User;
import com.mediai.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<Notification> getNotificationsForUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Notification> getTopNotifications(Long userId) {
        return notificationRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public Notification createNotification(User user, String title, String message, Notification.Type type, String link) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .link(link)
                .isRead(false)
                .build();
        return notificationRepository.save(notification);
    }

    @Transactional
    public boolean markAsRead(Long notificationId) {
        return notificationRepository.findById(notificationId).map(n -> {
            n.setIsRead(true);
            notificationRepository.save(n);
            return true;
        }).orElse(false);
    }
}
