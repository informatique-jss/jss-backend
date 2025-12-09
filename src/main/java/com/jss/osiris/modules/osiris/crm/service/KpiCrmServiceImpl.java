package com.jss.osiris.modules.osiris.crm.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.crm.model.IKpiThread;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValue;
import com.jss.osiris.modules.osiris.crm.repository.KpiCrmRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
public class KpiCrmServiceImpl implements KpiCrmService {

    @Autowired
    List<? extends IKpiThread> kpiThreadList;

    @Autowired
    BatchService batchService;

    @Autowired
    KpiCrmRepository kpiCrmRepository;

    @Autowired
    KpiCrmValueService kpiCrmValueService;

    @Autowired
    KpiCrmCategoryService kpiCrmCategoryService;

    @PersistenceContext
    EntityManager em;

    public KpiCrm addOrUpdateKpiCrm(KpiCrm kpiCrm) {
        return kpiCrmRepository.save(kpiCrm);
    }

    @Override
    public List<KpiCrm> getKpiCrms() {
        return kpiCrmRepository.findAllByOrderByDisplayOrderAsc();
    }

    @Override
    public KpiCrm getKpiCrm(Integer id) {
        Optional<KpiCrm> kpiCrm = kpiCrmRepository.findById(id);
        if (kpiCrm.isPresent())
            return kpiCrm.get();
        return null;
    }

    @Override
    public KpiCrm getKpiCrmByCode(String code) {
        return kpiCrmRepository.findByCode(code);
    }

    @Override
    public IKpiThread getKpiThread(KpiCrm kpiCrm) {
        if (kpiThreadList != null && kpiCrm != null)
            for (IKpiThread thread : kpiThreadList)
                if (thread.getCode().equals(kpiCrm.getCode()))
                    return thread;
        return null;
    }

    @Override
    public void computeKpis() throws OsirisException {
        List<KpiCrm> kpiList = getKpiCrms();

        for (KpiCrm kpi : kpiList)
            batchService.declareNewBatch(Batch.COMPUTE_KPI_CRM, kpi.getId());
    }

    /**
     * Method called by batch to persist the KpiValues of a KpiCrm
     * 
     * @throws OsirisException
     */
    @Override
    @Transactional
    public void computeKpiCrm(Integer kpiId) throws OsirisException {
        KpiCrm kpiCrm = getKpiCrm(kpiId);
        if (kpiCrm != null) {
            IKpiThread kpiThread = getKpiThread(kpiCrm);
            updateKpiCrmFromThread(kpiCrm, kpiThread);
            if (kpiCrm != null && kpiThread != null) {
                kpiThread.computeKpiCrmValues();
                kpiCrm.setLastUpdate(LocalDateTime.now());
                addOrUpdateKpiCrm(kpiCrm);
            }
        }
    }

    private void updateKpiCrmFromThread(KpiCrm kpiCrm, IKpiThread kpiThread) {
        if (kpiThread.getKpiCrmCategoryCode() != null)
            kpiCrm.setKpiCrmCategory(kpiCrmCategoryService.getKpiCrmCategoryByCode(kpiThread.getKpiCrmCategoryCode()));
        kpiCrm.setDefaultValue(kpiThread.getDefaultValue());
        kpiCrm.setIcon(kpiThread.getIcon());
        kpiCrm.setDisplayOrder(kpiThread.getDisplayOrder());
        kpiCrm.setIsPositiveEvolutionGood(kpiThread.getIsPositiveEvolutionGood());
        kpiCrm.setLabelType(kpiThread.getLabelType());
        kpiCrm.setUnit(kpiThread.getUnit());
        kpiCrm.setAggregateTypeForTimePeriod(kpiThread.getAggregateTypeForTimePeriod());
        kpiCrm.setGraphType(kpiThread.getGraphType());
        addOrUpdateKpiCrm(kpiCrm);
    }

    @Override
    public void saveValuesForKpiAndDay(KpiCrm kpiCrm, List<KpiCrmValue> values) {
        createPartitionsIfNotExist(kpiCrm);
        for (KpiCrmValue kpiCrmValue : values) {
            kpiCrmValue.setKpiCrm(kpiCrm);
        }
        kpiCrmValueService.addOrUpdateKpiCrmValues(values);
    }

    @Modifying
    private void createPartitionsIfNotExist(KpiCrm kpiCrm) {
        String tableIdPartitionName = "kpi_crm_value_kpi_" + kpiCrm.getId();

        em.createNativeQuery(
                "CREATE TABLE IF NOT EXISTS " + tableIdPartitionName + " PARTITION OF kpi_crm_value FOR VALUES IN ("
                        + kpiCrm.getId() + ") PARTITION BY RANGE (value_date)")
                .executeUpdate();

        // Creating the past tables until thisYear-1
        for (int year = 2023; year < LocalDate.now().getYear(); year++) {
            for (int month = 1; month <= 12; month++) {
                createDatePartition(tableIdPartitionName, year, month);
            }
        }

        // Creating the past tables of thisYear until thisMonth
        for (int month = 1; month <= LocalDate.now().getMonthValue(); month++) {
            createDatePartition(tableIdPartitionName, LocalDate.now().getYear(), month);
        }
        kpiCrm.setLastUpdate(LocalDateTime.now());

        // If thisMonth changed, creating the last partition for thisMonth
        if (kpiCrm.getLastUpdate().getMonth().compareTo(LocalDate.now().getMonth()) < 0) {
            createDatePartition(tableIdPartitionName, LocalDate.now().getYear(), LocalDate.now().getMonthValue());
        }
    }

    private void createDatePartition(String tableIdPartitionName, int year, int month) {
        String tableName = tableIdPartitionName + "_" + year + "_" + month;
        LocalDate startRange = LocalDate.of(year, month, 1);
        LocalDate endRange = startRange.plusMonths(1);

        Query q = em.createNativeQuery(
                "CREATE TABLE IF NOT EXISTS " + tableName + " PARTITION OF " + tableIdPartitionName
                        + " FOR VALUES FROM ('" + startRange + "') TO ('" + endRange + "');");
        q.setFlushMode(FlushModeType.COMMIT);
        q.executeUpdate();
    }

}
