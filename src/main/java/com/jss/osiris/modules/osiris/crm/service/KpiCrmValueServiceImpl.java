package com.jss.osiris.modules.osiris.crm.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.crm.model.IKpiThread;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValue;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValueAggregated;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValueAggregatedByResponsable;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValueAggregatedByTiers;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValuePayload;
import com.jss.osiris.modules.osiris.crm.repository.KpiCrmValueRepository;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Service
public class KpiCrmValueServiceImpl implements KpiCrmValueService {

    @Autowired
    KpiCrmValueRepository kpiCrmValueRepository;

    @Autowired
    KpiCrmService kpiCrmService;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public KpiCrmValue getKpiCrmValue(Integer id) {
        Optional<KpiCrmValue> kpiCrmValue = kpiCrmValueRepository.findById(id);
        if (kpiCrmValue.isPresent())
            return kpiCrmValue.get();
        return null;
    }

    @Override
    public void addOrUpdateKpiCrmValues(List<KpiCrmValue> kpiCrmValues) {
        kpiCrmValueRepository.saveAll(kpiCrmValues);
    }

    @Override
    public KpiCrmValue getLastCrmValue(KpiCrm kpiCrm) {
        return kpiCrmValueRepository.findFirstByKpiCrmOrderByValueDateDesc(kpiCrm);
    }

    private BigDecimal getKpiCrmValueAggregated(KpiCrm kpiCrm, List<Tiers> tiersList,
            LocalDate startDate, LocalDate endDate, Integer salesEmployeeId, boolean isAllTiers) {
        IKpiThread kpi = kpiCrmService.getKpiThread(kpiCrm);
        if (kpi != null) {

            String sql = """
                    select %s(value) as value
                    from (
                        select t.id,v.value_date , %s(v.value) as value
                        from kpi_crm_value v
                        join responsable r  on r.id = v.id_responsable
                        join tiers t  on t.id = r.id_tiers
                        where v.value_date>=:startDate
                        %s
                        and v.value_date<=:endDate
                        and v.id_kpi=:kpiCrm
                        and (:salesEmployeeId=0 or r.id_commercial = :salesEmployeeId)
                        group by t.id, v.value_date)
                      """.formatted(getSqlAggregateMethod(kpi.getAggregateTypeForTimePeriod()),
                    getSqlAggregateMethod(kpi.getAggregateTypeForResponsable()),
                    isAllTiers ? "" : " and  r.id_tiers in (:tiersList) ");
            ;

            @SuppressWarnings("unchecked")
            TypedQuery<KpiCrmValueAggregated> q = (TypedQuery<KpiCrmValueAggregated>) entityManager
                    .createNativeQuery(sql, KpiCrmValueAggregated.class);
            if (!isAllTiers)
                q.setParameter("tiersList", tiersList.stream().map(t -> t.getId()).toList());
            q.setParameter("startDate", startDate);
            q.setParameter("endDate", endDate);
            q.setParameter("salesEmployeeId", salesEmployeeId != null ? salesEmployeeId : 0);
            q.setParameter("kpiCrm", kpiCrm.getId());

            return q.getSingleResult().getValue();
        }

        return null;
    }

    private List<KpiCrmValueAggregatedByTiers> getKpiCrmValueAggregatedByTiers(KpiCrm kpiCrm, List<Tiers> tiersList,
            LocalDate startDate, LocalDate endDate, Integer salesEmployeeId) {
        IKpiThread kpi = kpiCrmService.getKpiThread(kpiCrm);
        if (kpi != null) {

            String sql = """
                    select id as idTiers, %s(value) as value
                    from (
                        select t.id,v.value_date , %s(v.value) as value
                        from kpi_crm_value v
                        join responsable r  on r.id = v.id_responsable
                        join tiers t  on t.id = r.id_tiers
                        where r.id_tiers in (:tiersList)
                        and v.value_date>=:startDate
                        and v.value_date<=:endDate
                        and (:salesEmployeeId=0 or r.id_commercial = :salesEmployeeId)
                        and v.id_kpi=:kpiCrm
                        group by t.id, v.value_date)
                    group by id
                      """.formatted(getSqlAggregateMethod(kpi.getAggregateTypeForTimePeriod()),
                    getSqlAggregateMethod(kpi.getAggregateTypeForResponsable()));
            ;

            @SuppressWarnings("unchecked")
            TypedQuery<KpiCrmValueAggregatedByTiers> q = (TypedQuery<KpiCrmValueAggregatedByTiers>) entityManager
                    .createNativeQuery(sql, KpiCrmValueAggregatedByTiers.class);
            q.setParameter("tiersList", tiersList.stream().map(t -> t.getId()).toList());
            q.setParameter("startDate", startDate);
            q.setParameter("endDate", endDate);
            q.setParameter("salesEmployeeId", salesEmployeeId != null ? salesEmployeeId : 0);
            q.setParameter("kpiCrm", kpiCrm.getId());

            return q.getResultList();
        }

        return null;
    }

    private KpiCrmValuePayload getKpiCrmValuePayloadAggregatedByTiersAndDate(KpiCrm kpiCrm, List<Tiers> tiersList,
            LocalDate startDate, LocalDate endDate, Integer salesEmployeeId, boolean isAllTiers,
            boolean aggregateResponsable) {
        IKpiThread kpi = kpiCrmService.getKpiThread(kpiCrm);
        if (kpi != null) {

            String sql = """
                    select t.id,v.value_date , %s(v.value) as value
                       from kpi_crm_value v
                       join responsable r  on r.id = v.id_responsable
                       join tiers t  on t.id = r.id_tiers
                       where v.value_date>=:startDate
                       %s
                       and v.value_date<=:endDate
                       and (:salesEmployeeId=0 or r.id_commercial = :salesEmployeeId)
                       and v.id_kpi=:kpiCrm
                       group by t.id, v.value_date
                     """.formatted(getSqlAggregateMethod(kpi.getAggregateTypeForTimePeriod()),
                    isAllTiers ? "" : " and r.id_tiers in (:tiersList) ");
            ;

            if (aggregateResponsable) {
                sql = ("select value_date, %s(value) as value from (" + sql + ") group by value_date")
                        .formatted(getSqlAggregateMethod(kpi.getAggregateTypeForResponsable()));
            }

            String finalSql = String.format("""
                    select
                        json_agg(
                            jsonb_build_object(
                                'data', data_points,
                                'label', '%s',
                                'type', '%s'
                            )
                        ) as json
                    from (
                        select
                            json_agg(jsonb_build_array(value_date, value) order by value_date) as data_points
                        from (%s)
                    );
                     """, kpiCrm.getLabel(), kpiCrm.getGraphType(), sql);

            @SuppressWarnings("unchecked")
            TypedQuery<KpiCrmValuePayload> q = (TypedQuery<KpiCrmValuePayload>) entityManager
                    .createNativeQuery(finalSql, KpiCrmValuePayload.class);
            if (!isAllTiers)
                q.setParameter("tiersList", tiersList.stream().map(t -> t.getId()).toList());
            q.setParameter("startDate", startDate);
            q.setParameter("endDate", endDate);
            q.setParameter("salesEmployeeId", salesEmployeeId != null ? salesEmployeeId : 0);
            q.setParameter("kpiCrm", kpiCrm.getId());

            return q.getSingleResult();
        }

        return null;
    }

    private String getSqlAggregateMethod(String aggregateType) {
        return switch (aggregateType) {
            case KpiCrm.AGGREGATE_TYPE_SUM -> "sum";
            case KpiCrm.AGGREGATE_TYPE_AVERAGE -> "avg";
            default -> "sum";
        };
    }

    @Override
    public List<KpiCrmValueAggregatedByResponsable> getAggregateValuesForResponsableListByResponsable(KpiCrm kpiCrm,
            LocalDate startDate, LocalDate endDate, Integer salesEmployeeId, List<Responsable> responsableList) {
        if (kpiCrm != null)
            return getKpiCrmValueAggregatedByResponsable(kpiCrm, responsableList, startDate, endDate, salesEmployeeId);
        return null;
    }

    @Override
    public BigDecimal getAggregateValuesForResponsableList(KpiCrm kpiCrm, LocalDate startDate,
            LocalDate endDate, Integer salesEmployeeId, List<Responsable> responsableList, boolean isAllTiers) {
        if (kpiCrm != null)
            return getKpiCrmValueAggregatedbyResponsable(kpiCrm, responsableList, startDate, endDate, salesEmployeeId,
                    isAllTiers);
        return null;
    }

    @Override
    public KpiCrmValuePayload getKpiCrmValuePayloadAggregatedByResponsableAndDate(KpiCrm kpiCrm, LocalDate startDate,
            LocalDate endDate, Integer salesEmployeeId, List<Responsable> responsableList, boolean isAllTiers,
            boolean aggregateResponsable) {
        if (kpiCrm != null)
            return getKpiCrmValuePayloadAggregatedByResponsableAndDate(kpiCrm, responsableList, startDate, endDate,
                    salesEmployeeId,
                    isAllTiers, aggregateResponsable);
        return null;
    }

    private BigDecimal getKpiCrmValueAggregatedbyResponsable(KpiCrm kpiCrm, List<Responsable> responsableList,
            LocalDate startDate, LocalDate endDate, Integer salesEmployeeId, boolean isAllTiers) {
        IKpiThread kpi = kpiCrmService.getKpiThread(kpiCrm);
        if (kpi != null) {

            String sql = """
                    select %s(value) as value
                    from (
                        select r.id,v.value_date , %s(v.value) as value
                        from kpi_crm_value v
                        join responsable r  on r.id = v.id_responsable
                        where v.value_date>=:startDate
                        %s
                        and v.value_date<=:endDate
                        and (:salesEmployeeId=0 or r.id_commercial = :salesEmployeeId)
                        and v.id_kpi=:kpiCrm
                        group by r.id, v.value_date)
                      """.formatted(getSqlAggregateMethod(kpi.getAggregateTypeForTimePeriod()),
                    getSqlAggregateMethod(kpi.getAggregateTypeForResponsable()),
                    isAllTiers ? "" : " and  r.id in (:responsableList) ");
            ;

            @SuppressWarnings("unchecked")
            TypedQuery<KpiCrmValueAggregated> q = (TypedQuery<KpiCrmValueAggregated>) entityManager
                    .createNativeQuery(sql, KpiCrmValueAggregated.class);
            if (!isAllTiers)
                q.setParameter("responsableList", responsableList.stream().map(t -> t.getId()).toList());
            q.setParameter("startDate", startDate);
            q.setParameter("endDate", endDate);
            q.setParameter("salesEmployeeId", salesEmployeeId != null ? salesEmployeeId : 0);
            q.setParameter("kpiCrm", kpiCrm.getId());

            return q.getSingleResult().getValue();
        }

        return null;
    }

    private List<KpiCrmValueAggregatedByResponsable> getKpiCrmValueAggregatedByResponsable(KpiCrm kpiCrm,
            List<Responsable> responsableList, LocalDate startDate, LocalDate endDate, Integer salesEmployeeId) {
        IKpiThread kpi = kpiCrmService.getKpiThread(kpiCrm);
        if (kpi != null) {

            String sql = """
                    select id as idResponsable, %s(value) as value
                    from (
                        select r.id,v.value_date , %s(v.value) as value
                        from kpi_crm_value v
                        join responsable r  on r.id = v.id_responsable
                        where r.id in (:responsableList)
                        and v.value_date>=:startDate
                        and v.value_date<=:endDate
                        and v.id_kpi=:kpiCrm
                        and (:salesEmployeeId=0 or r.id_commercial = :salesEmployeeId)
                        group by r.id, v.value_date)
                    group by id
                      """.formatted(getSqlAggregateMethod(kpi.getAggregateTypeForTimePeriod()),
                    getSqlAggregateMethod(kpi.getAggregateTypeForResponsable()));
            ;

            @SuppressWarnings("unchecked")
            TypedQuery<KpiCrmValueAggregatedByResponsable> q = (TypedQuery<KpiCrmValueAggregatedByResponsable>) entityManager
                    .createNativeQuery(sql, KpiCrmValueAggregatedByResponsable.class);
            q.setParameter("responsableList", responsableList.stream().map(t -> t.getId()).toList());
            q.setParameter("startDate", startDate);
            q.setParameter("endDate", endDate);
            q.setParameter("salesEmployeeId", salesEmployeeId != null ? salesEmployeeId : 0);
            q.setParameter("kpiCrm", kpiCrm.getId());

            return q.getResultList();
        }

        return null;
    }

    private KpiCrmValuePayload getKpiCrmValuePayloadAggregatedByResponsableAndDate(KpiCrm kpiCrm,
            List<Responsable> responsableList, LocalDate startDate, LocalDate endDate, Integer salesEmployeeId,
            boolean isAllTiers, boolean aggregateResponsable) {
        IKpiThread kpi = kpiCrmService.getKpiThread(kpiCrm);
        if (kpi != null) {

            String sql = """
                    select r.id,v.value_date , %s(v.value) as value
                       from kpi_crm_value v
                       join responsable r  on r.id = v.id_responsable
                       where v.value_date>=:startDate
                       %s
                       and v.value_date<=:endDate
                       and v.id_kpi=:kpiCrm
                       and (:salesEmployeeId=0 or r.id_commercial = :salesEmployeeId)
                       group by r.id, v.value_date
                     """.formatted(getSqlAggregateMethod(kpi.getAggregateTypeForTimePeriod()),
                    isAllTiers ? "" : " and r.id in (:responsableList) ");
            ;

            if (aggregateResponsable) {
                sql = ("select value_date, %s(value) as value from (" + sql + ") group by value_date")
                        .formatted(getSqlAggregateMethod(kpi.getAggregateTypeForResponsable()));
            }

            String finalSql = String.format("""
                    select
                        json_agg(
                            jsonb_build_object(
                                'data', data_points,
                                'label', '%s',
                                'type', '%s'
                            )
                        ) as json
                    from (
                        select
                            json_agg(jsonb_build_array(value_date, value) order by value_date) as data_points
                        from (%s)
                    );
                     """, kpiCrm.getLabel(), kpiCrm.getGraphType(), sql);

            @SuppressWarnings("unchecked")
            TypedQuery<KpiCrmValuePayload> q = (TypedQuery<KpiCrmValuePayload>) entityManager
                    .createNativeQuery(finalSql, KpiCrmValuePayload.class);
            if (!isAllTiers)
                q.setParameter("responsableList", responsableList.stream().map(t -> t.getId()).toList());
            q.setParameter("startDate", startDate);
            q.setParameter("endDate", endDate);
            q.setParameter("salesEmployeeId", salesEmployeeId != null ? salesEmployeeId : 0);
            q.setParameter("kpiCrm", kpiCrm.getId());

            return q.getSingleResult();
        }

        return null;
    }

    @Override
    public List<KpiCrmValueAggregatedByTiers> getAggregateValuesForTiersListByTiers(KpiCrm kpiCrm, LocalDate startDate,
            LocalDate endDate, Integer salesEmployeeId, List<Tiers> tiersList) {
        if (kpiCrm != null)
            return getKpiCrmValueAggregatedByTiers(kpiCrm, tiersList, startDate, endDate, salesEmployeeId);
        return null;
    }

    @Override
    public BigDecimal getAggregateValuesForTiersList(KpiCrm kpiCrm, LocalDate startDate,
            LocalDate endDate, Integer salesEmployeeId, List<Tiers> tiersList, boolean isAllTiers) {
        if (kpiCrm != null)
            return getKpiCrmValueAggregated(kpiCrm, tiersList, startDate, endDate, salesEmployeeId, isAllTiers);
        return null;
    }

    @Override
    public KpiCrmValuePayload getKpiCrmValuePayloadAggregatedByTiersAndDate(KpiCrm kpiCrm, LocalDate startDate,
            LocalDate endDate, Integer salesEmployeeId, List<Tiers> tiersList, boolean isAllTiers,
            boolean aggregateResponsable) {
        if (kpiCrm != null)
            return getKpiCrmValuePayloadAggregatedByTiersAndDate(kpiCrm, tiersList, startDate, endDate, salesEmployeeId,
                    isAllTiers, aggregateResponsable);
        return null;
    }
}
