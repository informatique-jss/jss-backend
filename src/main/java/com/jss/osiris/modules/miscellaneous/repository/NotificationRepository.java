package com.jss.osiris.modules.miscellaneous.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.miscellaneous.model.Notification;
import com.jss.osiris.modules.profile.model.Employee;

public interface NotificationRepository extends CrudRepository<Notification, Integer> {

    List<Notification> findByEmployee(Employee currentEmployee);
}