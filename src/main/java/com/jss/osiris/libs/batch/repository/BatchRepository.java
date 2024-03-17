package com.jss.osiris.libs.batch.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.model.BatchSettings;
import com.jss.osiris.libs.batch.model.BatchStatus;
import com.jss.osiris.libs.batch.model.IBatchStatistics;
import com.jss.osiris.libs.batch.model.IBatchTimeStatistics;
import com.jss.osiris.libs.node.model.Node;

public interface BatchRepository extends CrudRepository<Batch, Integer> {

        List<Batch> findTop1ByBatchSettingsAndBatchStatusOrderByCreatedDate(BatchSettings batchSetting,
                        BatchStatus batchStatusByCode);

        List<Batch> findByBatchSettingsAndBatchStatus(BatchSettings batchSetting, BatchStatus batchStatus);

        List<Batch> findByBatchSettingsAndEntityIdAndBatchStatusIn(BatchSettings batchSetting, Integer entityId,
                        List<BatchStatus> batchStatus);

        List<Batch> findByBatchSettingsAndBatchStatusAndNode(BatchSettings byId, BatchStatus batchStatusByCode,
                        Node currentNode);

        @Query(nativeQuery = true, value = "" +
                        "   select b.id_batch_settings as idBatchSettings,  " +
                        "   sum(case when bs.code = :batchStatusNewCode then 1 else 0 end ) as new , " +
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
                        "   where b.created_date\\:\\:date>= now()\\:\\:date-8 " +
                        "   group by b.id_batch_settings order by b.id_batch_settings limit 10000  " +
                        "")
        List<IBatchStatistics> getStatisticsOfBatch(
                        @Param("batchStatusNewCode") String batchStatusNewCode,
                        @Param("batchStatusSuccessCode") String batchStatusSuccessCode,
                        @Param("batchStatusWaitingCode") String batchStatusWaitingCode,
                        @Param("batchStatusRunningCode") String batchStatusRunningCode,
                        @Param("batchStatusErrorCode") String batchStatusErrorCode,
                        @Param("batchStatusAcknowledgeCode") String batchStatusAcknowledgeCode);

        @Query(nativeQuery = true, value = "" +
                        " select " +
                        " 	date_trunc('hour' ,(b.start_date)) as dateTime , " +
                        " 	count(b.id) as nbr, " +
                        " 	case when max(case when bs.code = :batchStatusErrorCode then 1 else 0 end )=1 then true else false end as hasError , "
                        +
                        " 	extract(EPOCH " +
                        " from " +
                        " 	avg(case when b.end_date is not null then b.end_date-b.start_date end)) as meanTime " +
                        " from " +
                        " 	batch b " +
                        " join batch_status bs on " +
                        " 	bs.id = b.id_batch_status " +
                        " where " +
                        " 	b.start_date >= now()\\:\\:date-8 and b.id_batch_settings = :batchSettingsId " +
                        " group by " +
                        " 	date_trunc('hour' ,(b.start_date) ) order by dateTime " +
                        "")
        List<IBatchTimeStatistics> getTimeStatisticsOfBatch(@Param("batchSettingsId") Integer batchSettingsId,
                        @Param("batchStatusErrorCode") String batchStatusErrorCode);

        @Query("select b from Batch b where  cast( created_date as date)  between :startDate and :endDate and (coalesce(:batchSettings) is null or b.batchSettings in :batchSettings) and (coalesce(:batchStatus) is null or b.batchStatus in :batchStatus)  and (coalesce(:nodes) is null or b.node in :nodes)  and (:entityId is null or b.entityId = :entityId)  order by createdDate desc ")
        List<Batch> searchBatchs(@Param("startDate") Date startDate,
                        @Param("endDate") Date endDate,
                        @Param("batchSettings") List<BatchSettings> batchSettings,
                        @Param("batchStatus") List<BatchStatus> batchStatus,
                        @Param("entityId") Integer entityId,
                        @Param("nodes") List<Node> nodes);

}