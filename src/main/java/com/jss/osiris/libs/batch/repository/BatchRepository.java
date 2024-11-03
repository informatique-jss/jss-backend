package com.jss.osiris.libs.batch.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
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

        @Query(nativeQuery = true, value = "select b.* " +
                        " from batch b  " +
                        "where created_date  between :startDate and :endDate " +
                        "and (coalesce(:batchSettings) =0 or b.id_batch_settings in :batchSettings) " +
                        "and (coalesce(:batchStatus) =0 or b.id_batch_status in :batchStatus) " +
                        "and (coalesce(:nodes) =0 or b.id_node in :nodes) " +
                        "and (:entityId =0 or b.entity_id  = :entityId)  order by created_date  desc ")
        List<Batch> searchBatchs(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        @Param("batchSettings") List<Integer> batchSettings,
                        @Param("batchStatus") List<Integer> batchStatus,
                        @Param("entityId") Integer entityId,
                        @Param("nodes") List<Integer> nodes);

        @Query(value = "select * from batch n where n.created_date<(now() - make_interval(months => :monthNbr))  ", nativeQuery = true)
        List<Batch> findBatchOlderThanMonths(@Param("monthNbr") Integer monthNbr);

        @Modifying
        @Query(nativeQuery = true, value = "" +
                        " drop table reprise_inpi_del; " +
                        " create table reprise_inpi_del as " +
                        " select distinct i.id " +
                        " from invoice i " +
                        " join customer_order co on co.id = i.id_customer_order_for_inbound_invoice " +
                        " join provision p on p.id = i.id_provision  " +
                        " join formalite_guichet_unique fgu on fgu.id_formalite  = p.id_formalite  " +
                        " where co.id_customer_order_status not in (13,12) " +
                        " and i.id_provider=1279 " +
                        " and not exists (select 1 from cart c where c.id_invoice = i.id) ")
        void createTablePurgeInvoice();

}