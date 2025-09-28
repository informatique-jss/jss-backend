package com.jss.osiris.modules.osiris.reporting.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.reporting.model.ReportingWidget;
import com.jss.osiris.modules.osiris.reporting.model.ReportingWorkingTable;
import com.jss.osiris.modules.osiris.reporting.repository.ReportingWorkingTableRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class ReportingWorkingTableServiceImpl implements ReportingWorkingTableService {

    private String tablePrefix = "reporting_";

    @Autowired
    ReportingWorkingTableRepository reportingWorkingTableRepository;

    @PersistenceContext
    EntityManager em;

    @Autowired
    ReportingWidgetService reportingWidgetService;

    @Autowired
    BatchService batchService;

    @Override
    public List<ReportingWorkingTable> getReportingWorkingTables() {
        return IterableUtils.toList(reportingWorkingTableRepository.findAll());
    }

    @Override
    public ReportingWorkingTable getReportingWorkingTable(Integer id) {
        Optional<ReportingWorkingTable> reportingWorkingTable = reportingWorkingTableRepository.findById(id);
        if (reportingWorkingTable.isPresent())
            return reportingWorkingTable.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReportingWorkingTable addOrUpdateReportingWorkingTable(
            ReportingWorkingTable reportingWorkingTable) {

        em.createNativeQuery(
                "DROP MATERIALIZED VIEW IF EXISTS " + tablePrefix + reportingWorkingTable.getViewName() + " CASCADE")
                .executeUpdate();

        return reportingWorkingTableRepository.save(reportingWorkingTable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void computeReportingWorkingTable(String reportingFrequency) throws OsirisException {
        List<ReportingWorkingTable> tables = reportingWorkingTableRepository
                .findByReportingUpdateFrequency(reportingFrequency);

        if (tables != null)
            for (ReportingWorkingTable table : tables) {
                Boolean viewExists = (Boolean) em.createNativeQuery(
                        "SELECT EXISTS (SELECT 1 FROM pg_matviews WHERE matviewname = :name)")
                        .setParameter("name", tablePrefix + table.getViewName())
                        .getSingleResult();

                if (!viewExists) {
                    String createSql = "CREATE MATERIALIZED VIEW " + tablePrefix + table.getViewName() + " AS "
                            + table.getSqlText();
                    em.createNativeQuery(createSql).executeUpdate();
                } else
                    em.createNativeQuery("REFRESH MATERIALIZED VIEW " + tablePrefix + table.getViewName())
                            .executeUpdate();
            }

        List<ReportingWidget> widgets = reportingWidgetService.getReportingWidgetsByFrequency(reportingFrequency);
        if (widgets != null)
            for (ReportingWidget widget : widgets)
                batchService.declareNewBatch(Batch.COMPUTE_REPORTING_WIDGET, widget.getId());

    }
}
