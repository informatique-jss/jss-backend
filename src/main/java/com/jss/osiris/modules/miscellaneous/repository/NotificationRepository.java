package com.jss.osiris.modules.miscellaneous.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.miscellaneous.model.Notification;
import com.jss.osiris.modules.profile.model.Employee;

public interface NotificationRepository extends CrudRepository<Notification, Integer> {

    @Query("select n from Notification n where n.employee in (:employees) and n.notificationType!='PERSONNAL' or n.employee=:currentEmployee and n.notificationType='PERSONNAL' and (n.createdDateTime<now() or :displayFuture=true) ")
    List<Notification> findByEmployees(@Param("employees") List<Employee> employees,
            @Param("currentEmployee") Employee currentEmployee, @Param("displayFuture") Boolean displayFuture);

    @Query(value = "select * from Notification n where n.created_date_time<(now() - make_interval(months => :monthNbr))  ", nativeQuery = true)
    List<Notification> findNotificationOlderThanMonths(@Param("monthNbr") Integer monthNbr);
}