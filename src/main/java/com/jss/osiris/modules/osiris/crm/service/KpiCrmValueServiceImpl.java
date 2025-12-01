package com.jss.osiris.modules.osiris.crm.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.crm.model.IKpiThread;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmSearchModel;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValue;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValueAggregated;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValueAggregatedByResponsable;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValueAggregatedByTiers;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValuePayload;
import com.jss.osiris.modules.osiris.crm.repository.KpiCrmValueRepository;

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

    @Override
    public LocalDate getLastKpiCrmValueDate(KpiCrm kpiCrm) {
        LocalDate lastDate = null;
        KpiCrmValue lastCrmValue = getLastCrmValue(kpiCrm);
        if (lastCrmValue != null)
            lastDate = lastCrmValue.getValueDate().plusDays(1);
        else
            lastDate = LocalDate.of(2023, 1, 1);
        return lastDate;
    }

    @Override
    public BigDecimal getKpiCrmValueAggregatedForTiersList(KpiCrm kpiCrm, KpiCrmSearchModel searchModel) {
        IKpiThread kpi = kpiCrmService.getKpiThread(kpiCrm);
        if (kpi != null) {

            String sql = """
                    select %s  as value
                    from (
                        select t.id,v.value_date , %s  as value, sum(weight) as weight
                        from kpi_crm_value v
                        join responsable r  on r.id = v.id_responsable
                        join tiers t  on t.id = r.id_tiers
                        where v.value_date>=:startDate
                        %s
                        and v.value_date<=:endDate
                        and v.id_kpi=:kpiCrm
                        and (:salesEmployeeId=0 or r.id_commercial = :salesEmployeeId)
                        group by t.id, v.value_date)
                      """.formatted(getAggregateString(kpi.getAggregateTypeForTimePeriod()),
                    getAggregateString(kpi.getAggregateTypeForResponsable()),
                    searchModel.isAllTiers() ? "" : " and  r.id_tiers in (:tiersList) ");
            ;

            @SuppressWarnings("unchecked")
            TypedQuery<KpiCrmValueAggregated> q = (TypedQuery<KpiCrmValueAggregated>) entityManager
                    .createNativeQuery(sql, KpiCrmValueAggregated.class);
            if (!searchModel.isAllTiers())
                q.setParameter("tiersList", searchModel.getTiersIds());
            q.setParameter("startDate", searchModel.getStartDateKpis());
            q.setParameter("endDate", searchModel.getEndDateKpis());
            q.setParameter("salesEmployeeId",
                    searchModel.getSalesEmployeeId() != null ? searchModel.getSalesEmployeeId() : 0);
            q.setParameter("kpiCrm", kpiCrm.getId());

            return q.getSingleResult().getValue();
        }

        return null;
    }

    @Override
    public List<KpiCrmValueAggregatedByTiers> getAggregateValuesForTiersListByTiers(KpiCrm kpiCrm,
            KpiCrmSearchModel searchModel) {
        IKpiThread kpi = kpiCrmService.getKpiThread(kpiCrm);
        if (kpi != null) {

            String sql = """
                    select id as idTiers, %s  as value
                    from (
                        select t.id,v.value_date , %s  as value, sum(weight) as weight
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
                      """.formatted(getAggregateString(kpi.getAggregateTypeForTimePeriod()),
                    getAggregateString(kpi.getAggregateTypeForResponsable()));
            ;

            @SuppressWarnings("unchecked")
            TypedQuery<KpiCrmValueAggregatedByTiers> q = (TypedQuery<KpiCrmValueAggregatedByTiers>) entityManager
                    .createNativeQuery(sql, KpiCrmValueAggregatedByTiers.class);
            q.setParameter("tiersList", searchModel.getTiersIds());
            q.setParameter("startDate", searchModel.getStartDateKpis());
            q.setParameter("endDate", searchModel.getEndDateKpis());
            q.setParameter("salesEmployeeId",
                    searchModel.getSalesEmployeeId() != null ? searchModel.getSalesEmployeeId() : 0);
            q.setParameter("kpiCrm", kpiCrm.getId());

            return q.getResultList();
        }

        return null;
    }

    @Override
    public KpiCrmValuePayload getKpiCrmValuePayloadAggregatedByTiersAndDate(KpiCrm kpiCrm,
            KpiCrmSearchModel searchModel,
            boolean aggregateResponsable) {
        IKpiThread kpi = kpiCrmService.getKpiThread(kpiCrm);
        if (kpi != null) {

            String scale = "day";
            if (searchModel.getKpiScale() != null)
                if (searchModel.getKpiScale().equals("WEEKLY"))
                    scale = "week";
            if (searchModel.getKpiScale().equals("MONTHLY"))
                scale = "month";

            String sql = """
                    select t.id,date_trunc('%s',v.value_date) as value_date , %s  as value, sum(weight) as weight
                       from kpi_crm_value v
                       join responsable r  on r.id = v.id_responsable
                       join tiers t  on t.id = r.id_tiers
                       where v.value_date>=:startDate
                       %s
                       and v.value_date<=:endDate
                       and (:salesEmployeeId=0 or r.id_commercial = :salesEmployeeId)
                       and v.id_kpi=:kpiCrm
                       group by t.id,date_trunc('%s',v.value_date)
                     """.formatted(scale, getAggregateString(kpi.getAggregateTypeForTimePeriod()),
                    searchModel.isAllTiers() ? "" : " and r.id_tiers in (:tiersList) ", scale);
            ;

            if (aggregateResponsable) {
                sql = ("select value_date, %s as value from (" + sql + ") group by value_date")
                        .formatted(getAggregateString(kpi.getAggregateTypeForResponsable()));
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
            if (!searchModel.isAllTiers())
                q.setParameter("tiersList", searchModel.getTiersIds());

            q.setParameter("startDate", searchModel.getStartDateKpis());
            q.setParameter("endDate", searchModel.getEndDateKpis());
            q.setParameter("salesEmployeeId",
                    searchModel.getSalesEmployeeId() != null ? searchModel.getSalesEmployeeId() : 0);
            q.setParameter("kpiCrm", kpiCrm.getId());

            return q.getSingleResult();
        }

        return null;
    }

    private String getAggregateString(String aggregateType) {
        if (aggregateType.equals(KpiCrm.AGGREGATE_TYPE_WEIGHTED_AVERAGE)) {
            return "sum(value*weight)/sum(weight)";
        } else
            return "%s(value)".formatted(getSqlAggregateMethod(aggregateType));
    }

    private String getSqlAggregateMethod(String aggregateType) {
        return switch (aggregateType) {
            case KpiCrm.AGGREGATE_TYPE_SUM -> "sum";
            case KpiCrm.AGGREGATE_TYPE_AVERAGE -> "avg";
            case KpiCrm.AGGREGATE_TYPE_LAST_VALUE -> "max";
            default -> "sum";
        };
    }

    @Override
    public List<KpiCrmValueAggregatedByResponsable> getAggregateValuesForResponsableListByResponsable(KpiCrm kpiCrm,
            KpiCrmSearchModel searchModel) {
        if (kpiCrm != null)
            return getKpiCrmValueAggregatedByResponsable(kpiCrm, searchModel);
        return null;
    }

    @Override
    public BigDecimal getAggregateValuesForResponsableList(KpiCrm kpiCrm, KpiCrmSearchModel searchModel) {
        IKpiThread kpi = kpiCrmService.getKpiThread(kpiCrm);
        if (kpi != null) {

            String sql = """
                    select %s  as value
                    from (
                        select r.id,v.value_date , %s  as value, sum(weight) as weight
                        from kpi_crm_value v
                        join responsable r  on r.id = v.id_responsable
                        where v.value_date>=:startDate
                        %s
                        and v.value_date<=:endDate
                        and (:salesEmployeeId=0 or r.id_commercial = :salesEmployeeId)
                        and v.id_kpi=:kpiCrm
                        group by r.id, v.value_date)
                      """.formatted(getAggregateString(kpi.getAggregateTypeForTimePeriod()),
                    getAggregateString(kpi.getAggregateTypeForResponsable()),
                    searchModel.isAllTiers() ? "" : " and  r.id in (:responsableList) ");
            ;

            @SuppressWarnings("unchecked")
            TypedQuery<KpiCrmValueAggregated> q = (TypedQuery<KpiCrmValueAggregated>) entityManager
                    .createNativeQuery(sql, KpiCrmValueAggregated.class);
            if (!searchModel.isAllTiers())
                q.setParameter("responsableList", searchModel.getResponsableIds());
            q.setParameter("startDate", searchModel.getStartDateKpis());
            q.setParameter("endDate", searchModel.getEndDateKpis());
            q.setParameter("salesEmployeeId",
                    searchModel.getSalesEmployeeId() != null ? searchModel.getSalesEmployeeId() : 0);
            q.setParameter("kpiCrm", kpiCrm.getId());

            return q.getSingleResult().getValue();
        }

        return null;
    }

    private List<KpiCrmValueAggregatedByResponsable> getKpiCrmValueAggregatedByResponsable(KpiCrm kpiCrm,
            KpiCrmSearchModel searchModel) {
        IKpiThread kpi = kpiCrmService.getKpiThread(kpiCrm);
        if (kpi != null) {

            String sql = """
                    select id as idResponsable, %s  as value
                    from (
                        select r.id,v.value_date , %s  as value, sum(weight) as weight
                        from kpi_crm_value v
                        join responsable r  on r.id = v.id_responsable
                        where r.id in (:responsableList)
                        and v.value_date>=:startDate
                        and v.value_date<=:endDate
                        and v.id_kpi=:kpiCrm
                        and (:salesEmployeeId=0 or r.id_commercial = :salesEmployeeId)
                        group by r.id, v.value_date)
                    group by id
                      """.formatted(getAggregateString(kpi.getAggregateTypeForTimePeriod()),
                    getAggregateString(kpi.getAggregateTypeForResponsable()));
            ;

            @SuppressWarnings("unchecked")
            TypedQuery<KpiCrmValueAggregatedByResponsable> q = (TypedQuery<KpiCrmValueAggregatedByResponsable>) entityManager
                    .createNativeQuery(sql, KpiCrmValueAggregatedByResponsable.class);
            q.setParameter("responsableList", searchModel.getResponsableIds());
            q.setParameter("startDate", searchModel.getStartDateKpis());
            q.setParameter("endDate", searchModel.getEndDateKpis());
            q.setParameter("salesEmployeeId",
                    searchModel.getSalesEmployeeId() != null ? searchModel.getSalesEmployeeId() : 0);
            q.setParameter("kpiCrm", kpiCrm.getId());

            return q.getResultList();
        }

        return null;
    }

    @Override
    public KpiCrmValuePayload getKpiCrmValuePayloadAggregatedByResponsableAndDate(KpiCrm kpiCrm,
            KpiCrmSearchModel searchModel, boolean aggregateResponsable) {
        IKpiThread kpi = kpiCrmService.getKpiThread(kpiCrm);
        if (kpi != null) {

            String sql = """
                    select r.id,v.value_date , %s  as value, sum(weight) as weight
                       from kpi_crm_value v
                       join responsable r  on r.id = v.id_responsable
                       where v.value_date>=:startDate
                       %s
                       and v.value_date<=:endDate
                       and v.id_kpi=:kpiCrm
                       and (:salesEmployeeId=0 or r.id_commercial = :salesEmployeeId)
                       group by r.id, v.value_date
                     """.formatted(getAggregateString(kpi.getAggregateTypeForTimePeriod()),
                    searchModel.isAllTiers() ? "" : " and r.id in (:responsableList) ");
            ;

            if (aggregateResponsable) {
                sql = ("select value_date, %s  as value from (" + sql + ") group by value_date")
                        .formatted(getAggregateString(kpi.getAggregateTypeForResponsable()));
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
            if (!searchModel.isAllTiers())
                q.setParameter("responsableList", searchModel.getResponsableIds());
            q.setParameter("startDate", searchModel.getStartDateKpis());
            q.setParameter("endDate", searchModel.getEndDateKpis());
            q.setParameter("salesEmployeeId",
                    searchModel.getSalesEmployeeId() != null ? searchModel.getSalesEmployeeId() : 0);
            q.setParameter("kpiCrm", kpiCrm.getId());

            return q.getSingleResult();
        }

        return null;
    }

    @Override
    public void deleteKpiCrmValuesForKpiCrm(KpiCrm kpiCrm) {
        kpiCrmValueRepository.deleteByKpiCrm(kpiCrm);
    }

}
