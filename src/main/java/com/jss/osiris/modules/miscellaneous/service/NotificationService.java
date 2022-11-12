package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.Notification;

public interface NotificationService {
    public List<Notification> getNotificationsForCurrentEmployee();

    public Notification getNotification(Integer id);

    public Notification addOrUpdateNotificationFromUser(Notification notification);

    public Notification addOrUpdateNotification(Notification notification);

    public void deleteNotification(Notification notification);
}
