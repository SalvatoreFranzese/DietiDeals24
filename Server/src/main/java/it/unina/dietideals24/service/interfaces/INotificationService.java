package it.unina.dietideals24.service.interfaces;

import it.unina.dietideals24.model.Notification;

import java.util.List;

public interface INotificationService {
    List<Notification> getNotificationsByReceiverId(Long receiverId);

    void deleteNotification(Long id);

    void saveAll(List<Notification> notifications);

    void save(Notification notification);

    List<Notification> getPushNotificationsByReceiverId(Long receiverId);
}
