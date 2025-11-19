package com.jss.osiris.modules.osiris.crm.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.crm.facade.KpiCrmFacade;
import com.jss.osiris.modules.osiris.crm.model.JobStatus;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmJob;

@Service
public class KpiCrmQueueService {

    private final ConcurrentMap<String, KpiCrmJob> jobs = new ConcurrentHashMap<>();
    private final ExecutorService worker = Executors.newSingleThreadExecutor();

    @Autowired
    KpiCrmFacade kpiCrmFacade;

    public KpiCrmJob submitJobForAggregateValuesForTiersList(String kpiCrmKey, LocalDate startDate,
            LocalDate endDate, Integer salesEmployeeId, List<Integer> tiersIds, boolean isAllTiers) {
        String jobId = UUID.randomUUID().toString();
        KpiCrmJob job = new KpiCrmJob(JobStatus.QUEUED);
        job.setId(jobId);
        jobs.put(jobId, job);

        worker.submit(() -> {
            KpiCrmJob jobInProgress = jobs.get(jobId);
            jobInProgress.setStatus(JobStatus.RUNNING);
            try {
                jobInProgress.setResult(
                        kpiCrmFacade.getAggregateValuesForTiersList(kpiCrmKey, startDate, endDate, salesEmployeeId,
                                tiersIds,
                                isAllTiers));
            } catch (Exception e) {
                System.out.println("Error on KPI Crm job");
            }
            jobInProgress.setStatus(JobStatus.DONE);
        });

        return job;
    }

    public KpiCrmJob submitJobForAggregateValuesForResponsableList(String kpiCrmKey, LocalDate startDate,
            LocalDate endDate, Integer salesEmployeeId, List<Integer> responsableIds, boolean isAllResponsable) {
        String jobId = UUID.randomUUID().toString();
        KpiCrmJob job = new KpiCrmJob(JobStatus.QUEUED);
        job.setId(jobId);
        jobs.put(jobId, job);

        worker.submit(() -> {
            KpiCrmJob jobInProgress = jobs.get(jobId);
            jobInProgress.setStatus(JobStatus.RUNNING);
            try {
                jobInProgress.setResult(
                        kpiCrmFacade.getAggregateValuesForResponsableList(kpiCrmKey, startDate, endDate,
                                salesEmployeeId, responsableIds,
                                isAllResponsable));
            } catch (Exception e) {
                System.out.println("Error on KPI Crm job");
            }
            jobInProgress.setStatus(JobStatus.DONE);
        });

        return job;
    }

    public KpiCrmJob getJobStatus(String jobId) {
        KpiCrmJob job = jobs.get(jobId);
        if (job != null && (job.getStatus().equals(JobStatus.ERROR) || job.getStatus().equals(JobStatus.DONE)))
            jobs.remove(jobId);
        return job;
    }
}