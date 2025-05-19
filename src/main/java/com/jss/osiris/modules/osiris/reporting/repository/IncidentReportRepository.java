package com.jss.osiris.modules.osiris.reporting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.reporting.model.IncidentReport;

public interface IncidentReportRepository extends QueryCacheCrudRepository<IncidentReport, Integer> {

        List<IncidentReport> findByCustomerOrder(CustomerOrder customerOrder);

        @Query("select i from IncidentReport i"
                        +
                        "  where (0 in :employees or i.assignedTo.id in :employees) " +
                        "    and (0 in :status or  i.incidentReportStatus.id in :status) order by i.startDate desc ")
        List<IncidentReport> searchIncidentReport(List<Integer> employees,
                        List<Integer> status);
}