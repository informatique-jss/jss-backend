package com.jss.osiris.modules.osiris.crm.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;

public interface KpiCrmRepository extends QueryCacheCrudRepository<KpiCrm, Integer> {

        KpiCrm findByCode(String code);

        @Query("""
                        SELECT SUM(k.value)
                        FROM KpiCrmValue k
                        WHERE k.responsable.id IN :responsables
                          AND k.kpiCrm.id = :kpiId
                          AND extract(DAY FROM k.valueDate) = :day
                          AND k.valueDate BETWEEN :startDate AND :endDate
                        """)
        BigDecimal findValueByDayOfMonthForResponsables(
                        @Param("responsables") List<Integer> responsables,
                        @Param("kpiId") Integer kpiId,
                        @Param("day") int day,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("""
                        SELECT SUM(k.value)
                        FROM KpiCrmValue k
                        WHERE k.responsable.id IN :responsables
                          AND k.kpiCrm.id = :kpiId
                          AND k.valueDate BETWEEN :startDate AND :endDate
                        """)
        BigDecimal findLastValueForResponsables(
                        @Param("responsables") List<Integer> responsables,
                        @Param("kpiId") Integer kpiId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        List<KpiCrm> getKpiCrmByDisplayedPage(String displayedPage);

}
