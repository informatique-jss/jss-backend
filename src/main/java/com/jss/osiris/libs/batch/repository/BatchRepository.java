package com.jss.osiris.libs.batch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.model.BatchSettings;
import com.jss.osiris.libs.batch.model.BatchStatus;
import com.jss.osiris.libs.batch.model.IBatchStatistics;
import com.jss.osiris.libs.node.model.Node;

public interface BatchRepository extends CrudRepository<Batch, Integer> {

        List<Batch> findTop1ByBatchSettingsAndBatchStatusOrderByCreatedDate(BatchSettings batchSetting,
                        BatchStatus batchStatusByCode);

        List<Batch> findByBatchSettingsAndBatchStatusAndNode(BatchSettings byId, BatchStatus batchStatusByCode,
                        Node currentNode);

        @Query(nativeQuery = true, value = "" +
                        "   select b.id_batch_settings as idBatchSettings,  " +
                        "   sum(case when bs.code = :batchStatusSuccessCode then 1 else 0 end ) as success , " +
                        "   sum(case when bs.code = :batchStatusWaitingCode then 1 else 0 end ) as waiting , " +
                        "   sum(case when bs.code = :batchStatusRunningCode then 1 else 0 end ) as running , " +
                        "   sum(case when bs.code = :batchStatusErrorCode then 1 else 0 end ) as error , " +
                        "   sum(case when bs.code = :batchStatusAcknowledgeCode then 1 else 0 end ) as acknowledge, "
                        +
                        "    EXTRACT(EPOCH from avg(case when b.end_date is not null then b.end_date-b.start_date end)) as meanTime, "
                        +
                        "    EXTRACT(EPOCH from avg( case when  b.end_date is not null and b.start_date between now()\\:\\:date-8 and now()\\:\\:date-1 then   b.end_date-b.start_date end)) as standardMeanTime, "
                        +
                        "    EXTRACT(EPOCH from avg( case when b.end_date is not null and b.start_date between now()\\:\\:date-1 and now() then   b.end_date-b.start_date end)) as currentMeanTime "
                        +
                        "   from batch b  " +
                        "   join batch_status bs on bs.id = b.id_batch_status  " +
                        "   where b.start_date>= now()\\:\\:date-8 " +
                        "   group by b.id_batch_settings  " +
                        "")
        List<IBatchStatistics> getStatisticsOfBatch(
                        @Param("batchStatusSuccessCode") String batchStatusSuccessCode,
                        @Param("batchStatusWaitingCode") String batchStatusWaitingCode,
                        @Param("batchStatusRunningCode") String batchStatusRunningCode,
                        @Param("batchStatusErrorCode") String batchStatusErrorCode,
                        @Param("batchStatusAcknowledgeCode") String batchStatusAcknowledgeCode);

}