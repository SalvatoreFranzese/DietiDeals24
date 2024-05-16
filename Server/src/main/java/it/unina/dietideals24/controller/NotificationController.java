package it.unina.dietideals24.controller;

import it.unina.dietideals24.model.Notification;
import it.unina.dietideals24.service.interfaces.INotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Qualifier("mainNotificationService")
    private final INotificationService notificationService;

    @Autowired
    public NotificationController(INotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("receiver/{id}")
    public List<Notification> getNotificationsByReceiverId(@PathVariable("id") Long receiverId) {
        return notificationService.getNotificationsByReceiverId(receiverId);
    }

    @GetMapping("receiver/{id}/push")
    public List<Notification> getPushNotificationByReceiverId(@PathVariable("id") Long receiverId) {
        return notificationService.getPushNotificationsByReceiverId(receiverId);
    }

    @DeleteMapping("{id}")
    public void deleteNotification(@PathVariable("id") Long id) {
        notificationService.deleteNotification(id);
    }
}
