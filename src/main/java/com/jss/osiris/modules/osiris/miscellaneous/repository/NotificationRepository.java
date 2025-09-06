package com.jss.osiris.modules.osiris.miscellaneous.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.crm.model.Candidacy;
import com.jss.osiris.modules.osiris.miscellaneous.model.Notification;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.Service;

public interface NotificationRepository extends QueryCacheCrudRepository<Notification, Integer> {

        @Query("select n from Notification n where n.employee in (:employees) " +
                        " and (coalesce(n.updatedDateTime,n.createdDateTime)<CURRENT_TIMESTAMP() or :displayFuture=true) "
                        +
                        " and (coalesce(n.isRead,false) = false or :displayRead=true)" +
                        " and n.notificationType not in (:notificationTypeToHide) and n.notificationType in (:notificationToDisplay) order by coalesce(updatedDateTime,'1970-01-01')  ")
        List<Notification> findByEmployees(@Param("employees") List<Employee> employees,
                        @Param("displayFuture") Boolean displayFuture,
                        @Param("displayRead") Boolean displayRead,
                        @Param("notificationTypeToHide") List<String> notificationTypeToHide,
                        @Param("notificationToDisplay") List<String> notificationToDisplay);

        @Query(value = "select * from Notification n where coalesce(n.is_read,false) = true and coalesce(n.updated_date_time,n.created_date_time)<(now() - make_interval(months => :monthNbr))  ", nativeQuery = true)
        List<Notification> findNotificationOlderThanMonths(@Param("monthNbr") Integer monthNbr);

        List<Notification> findByEmployeeAndNotificationTypeAndService(Employee toEmployee, String notificationType,
                        Service service);

        List<Notification> findByEmployeeAndNotificationTypeAndProvision(Employee toEmployee, String notificationType,
                        Provision provision);

        List<Notification> findByEmployeeAndNotificationTypeAndCustomerOrder(Employee toEmployee,
                        String notificationType, CustomerOrder customerOrder);

        List<Notification> findByEmployeeAndNotificationTypeAndCandidacy(Employee toEmployee,
                        String notificationType, Candidacy candidacy);

}