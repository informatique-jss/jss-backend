package com.jss.osiris.modules.osiris.reporting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.reporting.model.IncidentReport;
import com.jss.osiris.modules.osiris.reporting.model.IncidentReportStatus;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;

public interface IncidentReportRepository extends QueryCacheCrudRepository<IncidentReport, Integer> {

        List<IncidentReport> findByCustomerOrder(CustomerOrder customerOrder);

        @Query("select i from IncidentReport i"
                        +
                        "  where (0 in :employees or i.assignedTo.id in :employees) " +
                        "    and (0 in :status or  i.incidentReportStatus.id in :status) order by i.startDate desc ")
        List<IncidentReport> searchIncidentReport(List<Integer> employees,
                        List<Integer> status);

        @Query(" select i from IncidentReport i join i.customerOrder c where c.responsable=:responsable and i.incidentReportStatus not in (:incidentReportStatusAbandonned) ")
        List<IncidentReport> findIncidentReportByResponsable(Responsable responsable,
                        IncidentReportStatus incidentReportStatusAbandonned);

        @Query(" select i from IncidentReport i join i.customerOrder c join c.responsable r where r.tiers=:tiers and i.incidentReportStatus not in (:incidentReportStatusAbandonned) ")
        List<IncidentReport> findIncidentReportByTiers(Tiers tiers,
                        IncidentReportStatus incidentReportStatusAbandonned);
}