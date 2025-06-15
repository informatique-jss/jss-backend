package com.jss.osiris.modules.osiris.reporting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.reporting.model.Indicator;
import com.jss.osiris.modules.osiris.reporting.model.IndicatorValue;

public interface IndicatorValueRepository extends QueryCacheCrudRepository<IndicatorValue, Integer> {

  List<IndicatorValue> findByIndicatorOrderByDate(Indicator indicator);

  @Query("""
        SELECT iv FROM IndicatorValue iv join fetch iv.indicator i left join fetch i.kpis
        WHERE iv.employee = :employee
          AND iv.date = (
            SELECT MAX(sub.date) FROM IndicatorValue sub
            WHERE sub.employee = :employee
              AND sub.indicator.id = iv.indicator.id
          )
      """)
  List<IndicatorValue> findLatestIndicatorValuesForEmployee(@Param("employee") Employee employee);
}