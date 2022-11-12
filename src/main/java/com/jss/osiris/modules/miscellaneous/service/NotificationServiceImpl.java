package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.miscellaneous.model.Notification;
import com.jss.osiris.modules.miscellaneous.repository.NotificationRepository;
import com.jss.osiris.modules.profile.service.EmployeeService;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    EmployeeService employeeService;

    @Override
    public List<Notification> getNotificationsForCurrentEmployee() {
        return notificationRepository.findByEmployee(employeeService.getCurrentEmployee());
    }

    @Override
    public Notification getNotification(Integer id) {
        Optional<Notification> notification = notificationRepository.findById(id);
        if (notification.isPresent())
            return notification.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Notification addOrUpdateNotificationFromUser(
            Notification notification) {
        return addOrUpdateNotification(notification);
    }

    @Override
    public Notification addOrUpdateNotification(
            Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotification(Notification notification) {
        notificationRepository.delete(notification);
    }
}
