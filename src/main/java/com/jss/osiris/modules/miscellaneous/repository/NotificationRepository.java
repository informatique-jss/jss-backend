package com.jss.osiris.modules.miscellaneous.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.miscellaneous.model.Notification;
import com.jss.osiris.modules.profile.model.Employee;

public interface NotificationRepository extends CrudRepository<Notification, Integer> {

    @Query("select n from Notification n   where    COALESCE(:employees) is null or n.employee in (:employees)")
    List<Notification> findByEmployees(@Param("employees") List<Employee> employees);
}