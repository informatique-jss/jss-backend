package com.jss.osiris.libs.batch.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.GlobalExceptionHandler;
import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.model.BatchSearch;
import com.jss.osiris.libs.batch.model.BatchSettings;
import com.jss.osiris.libs.batch.model.BatchStatus;
import com.jss.osiris.libs.batch.model.IBatchStatistics;
import com.jss.osiris.libs.batch.model.IBatchTimeStatistics;
import com.jss.osiris.libs.batch.repository.BatchRepository;
import com.jss.osiris.libs.batch.service.threads.IOsirisThread;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisLog;
import com.jss.osiris.libs.node.model.Node;
import com.jss.osiris.libs.node.service.NodeService;

@Service
public class BatchServiceImpl implements BatchService, ApplicationListener<ContextClosedEvent> {

    @Autowired
    BatchRepository batchRepository;

    @Autowired
    BatchSettingsService batchSettingsService;

    @Autowired
    BatchStatusService batchStatusService;

    @Autowired
    NodeService nodeService;

    @Autowired
    GlobalExceptionHandler globalExceptionHandler;

    private HashMap<Integer, LocalDateTime> lastBatchCheck = new HashMap<Integer, LocalDateTime>();
    private HashMap<Integer, ThreadPoolTaskExecutor> queues = new HashMap<Integer, ThreadPoolTaskExecutor>();

    private boolean isShutingDown = false;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Batch addOrUpdateBatch(Batch batch) {
        return batchRepository.save(batch);
    }

    @Override
    public Batch getBatch(Integer id) {
        Optional<Batch> batch = batchRepository.findById(id);
        if (batch.isPresent())
            return batch.get();
        return null;
    }

    @Override
    public void checkBatch() throws OsirisException {
        List<BatchSettings> allBatchSettings = batchSettingsService.getAllBatchSettings();
        if (!isShutingDown && shouldIBatch() && allBatchSettings != null && allBatchSettings.size() > 0)
            for (BatchSettings batchSetting : allBatchSettings) {
                if (batchSetting.getIsActive()) { // is active
                    addOrUpdateQueue(batchSetting);
                    // Initialize queue and set settings
                    // Inject new jobs
                    if (lastBatchCheck.get(batchSetting.getId()) == null || lastBatchCheck.get(batchSetting.getId())
                            .plusSeconds(batchSetting.getFixedRate() / 1000).isBefore(LocalDateTime.now())) { // first
                                                                                                              // time or
                        // after rate
                        lastBatchCheck.put(batchSetting.getId(), LocalDateTime.now());
                        int numberAdded = 0;

                        List<Batch> batchs = batchRepository.findByBatchSettingsAndBatchStatus(batchSetting,
                                batchStatusService.getBatchStatusByCode(BatchStatus.NEW));
                        if (batchs != null && batchs.size() > 0)
                            for (Batch batch : batchs)
                                if (numberAdded < 1000 && (batchSetting.getMaxAddedNumberPerIteration() <= 0
                                        || batchSetting.getMaxAddedNumberPerIteration() > numberAdded)) {
                                    addBatchToQueue(batch);
                                    numberAdded++;
                                }
                    }
                }
            }
    }

    private boolean shouldIBatch() throws OsirisException {
        List<Node> nodes = nodeService.getAllNodes();
        Node myself = nodeService.getCurrentNode();
        for (Node node : nodes) {
            if (!node.getId().equals(myself.getId())
                    && node.getLastAliveDatetime().plusSeconds(30).isAfter(LocalDateTime.now())
                    && node.getBatchNodePriority() > myself.getBatchNodePriority())
                return false;
        }
        return true;
    }

    private void addOrUpdateQueue(BatchSettings batchSettings) {
        ThreadPoolTaskExecutor executor;
        if (queues.get(batchSettings.getId()) == null) {
            executor = new ThreadPoolTaskExecutor();
            executor.setThreadNamePrefix(batchSettings.getCode() + "-");
            executor.afterPropertiesSet();
            executor.setAwaitTerminationSeconds(10);
            queues.put(batchSettings.getId(), executor);
        }
        executor = queues.get(batchSettings.getId());
        executor.setCorePoolSize(batchSettings.getQueueSize());
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        isShutingDown = true;
        if (queues != null && queues.size() > 0) {
            for (Integer batchId : queues.keySet()) {
                ThreadPoolTaskExecutor executor = queues.get(batchId);
                BatchSettings batchSettings = batchSettingsService.getBatchSettings(batchId);
                executor.shutdown();

                // Reset batch that still running or waiting
                Node currentNode = null;
                try {
                    currentNode = nodeService.getCurrentNode();
                } catch (OsirisException e) {
                    globalExceptionHandler.handleExceptionOsiris(e);
                }

                List<Batch> batchsRunning = batchRepository.findByBatchSettingsAndBatchStatusAndNode(
                        batchSettingsService.getBatchSettings(batchId),
                        batchStatusService.getBatchStatusByCode(BatchStatus.RUNNING), currentNode);
                List<Batch> batchsWaiting = batchRepository.findByBatchSettingsAndBatchStatusAndNode(
                        batchSettingsService.getBatchSettings(batchId),
                        batchStatusService.getBatchStatusByCode(BatchStatus.WAITING), currentNode);

                System.out.println("Shuting down " + batchSettings.getCode() + " batch. Reset "
                        + (batchsRunning != null ? batchsRunning.size() : 0) + " batchs running and "
                        + (batchsWaiting != null ? batchsWaiting.size() : 0) + " batchs waiting.");

                if (batchsRunning != null && batchsRunning != null)
                    for (Batch batch : batchsRunning) {
                        batch.setBatchStatus(batchStatusService.getBatchStatusByCode(BatchStatus.NEW));
                        batch.setNode(null);
                        addOrUpdateBatch(batch);
                    }

                if (batchsWaiting != null && batchsWaiting != null)
                    for (Batch batch : batchsWaiting) {
                        batch.setBatchStatus(batchStatusService.getBatchStatusByCode(BatchStatus.NEW));
                        batch.setNode(null);
                        addOrUpdateBatch(batch);
                    }
            }
        }
    }

    @Autowired
    List<? extends IOsirisThread> IOsirisThreadList;

    private Future<?> addBatchToQueue(Batch batch) throws OsirisException {
        batch.setBatchStatus(batchStatusService.getBatchStatusByCode(BatchStatus.WAITING));
        batch.setNode(nodeService.getCurrentNodeCached());
        addOrUpdateBatch(batch);
        ThreadPoolTaskExecutor executor = queues.get(batch.getBatchSettings().getId());

        if (IOsirisThreadList != null && IOsirisThreadList.size() > 0)
            for (IOsirisThread batchThread : IOsirisThreadList)
                if (batch.getBatchSettings().getCode().equals(batchThread.getBatchCode()))
                    return executor.submit(getTask(batch, batchThread));

        throw new OsirisException(null, "Nothing declared for batch " + batch.getBatchSettings().getCode());
    }

    private BatchStatus batchStatusNew = null;
    private BatchStatus batchStatusWaiting = null;
    private HashMap<String, BatchSettings> batchSettings = new HashMap<String, BatchSettings>();

    @Override
    public Batch declareNewBatch(String batchCode, Integer entityId) throws OsirisException {
        Batch batch = new Batch();

        if (batchSettings.get(batchCode) == null)
            batchSettings.put(batchCode, batchSettingsService.getByCode(batchCode));

        if (batchStatusNew == null)
            batchStatusNew = batchStatusService.getBatchStatusByCode(BatchStatus.NEW);
        if (batchStatusWaiting == null)
            batchStatusWaiting = batchStatusService.getBatchStatusByCode(BatchStatus.WAITING);

        batch.setBatchSettings(batchSettings.get(batchCode));

        batch.setBatchStatus(batchStatusNew);

        batch.setCreatedDate(LocalDateTime.now());
        batch.setEntityId(entityId);
        batch.setNode(nodeService.getCurrentNodeCached());

        if (batch.getBatchSettings().getIsOnlyOneJob() == true) {
            List<Batch> batchs = new ArrayList<Batch>();
            batchs.addAll(batchRepository.findByBatchSettingsAndBatchStatus(batch.getBatchSettings(), batchStatusNew));
            batchs.addAll(
                    batchRepository.findByBatchSettingsAndBatchStatus(batch.getBatchSettings(), batchStatusWaiting));
            if (batchs != null && batchs.size() > 1)
                return null;
        }
        return addOrUpdateBatch(batch);
    }

    public Thread getTask(Batch batch, IOsirisThread thread) {
        batch.setBatchStatus(batchStatusService.getBatchStatusByCode(BatchStatus.RUNNING));
        batch.setStartDate(LocalDateTime.now());
        addOrUpdateBatch(batch);
        return new Thread(() -> {
            try {
                thread.executeTask(batch.getEntityId());
                batch.setBatchStatus(batchStatusService.getBatchStatusByCode(BatchStatus.SUCCESS));
            } catch (Exception e) {
                OsirisLog log = globalExceptionHandler.persistLog(e, OsirisLog.UNHANDLED_LOG);
                batch.setOsirisLog(log);
                batch.setBatchStatus(batchStatusService.getBatchStatusByCode(BatchStatus.ERROR));
            } finally {
                batch.setEndDate(LocalDateTime.now());
                addOrUpdateBatch(batch);
            }
        });
    }

    @Override
    public List<IBatchStatistics> getBatchStatistics() {
        return batchRepository.getStatisticsOfBatch(BatchStatus.SUCCESS, BatchStatus.WAITING, BatchStatus.RUNNING,
                BatchStatus.ERROR, BatchStatus.ACKNOWLEDGE);
    }

    @Override
    public List<IBatchTimeStatistics> getTimeStatisticsOfBatch(BatchSettings batchSettings) {
        return batchRepository.getTimeStatisticsOfBatch(batchSettings.getId(), BatchStatus.ERROR);
    }

    @Override
    public List<Batch> searchBatchs(BatchSearch batchSearch) {
        return batchRepository.searchBatchs(
                Date.from(batchSearch.getStartDate().atZone(ZoneId.systemDefault()).toInstant()),
                Date.from(batchSearch.getEndDate().atZone(ZoneId.systemDefault()).toInstant()),
                batchSearch.getBatchSettings(), batchSearch.getBatchStatus(), batchSearch.getEntityId(),
                batchSearch.getNodes());
    }
}
