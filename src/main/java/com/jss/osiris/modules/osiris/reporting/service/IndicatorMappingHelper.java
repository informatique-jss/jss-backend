package com.jss.osiris.modules.osiris.reporting.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.datasource.DataSource;
import com.jss.osiris.libs.datasource.DataSourceType;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.reporting.model.Indicator;
import com.jss.osiris.modules.osiris.reporting.model.IndicatorValue;

@Service
public class IndicatorMappingHelper {

    @Autowired
    EmployeeService employeeService;

    @Autowired
    IndicatorValueService indicatorValueService;

    @Autowired
    IndicatorService indicatorService;

    @Transactional(rollbackFor = Exception.class)
    @DataSource(DataSourceType.MASTER)
    protected void mapAndSaveValue(List<Map<String, Object>> results, Indicator indicator) {
        if (results != null) {
            indicatorValueService.deleteIndicatorValuesForIndicator(indicator);
            for (Map<String, Object> result : results) {
                Integer employeeId = (Integer) result.get("id_employee");
                LocalDate date = ((Timestamp) result.get("date")).toLocalDateTime().toLocalDate();
                BigDecimal value = new BigDecimal((Long) result.get("value"));

                Employee employee = null;
                if (employeeId != null)
                    employee = employeeService.getEmployee(employeeId);

                IndicatorValue indicatorValue = new IndicatorValue();
                indicatorValue.setIndicator(indicator);
                indicatorValue.setEmployee(employee);
                indicatorValue.setDate(date);
                indicatorValue.setValue(value);

                indicatorValueService.addOrUpdateIndicatorValue(indicatorValue);
            }

            indicator.setLastUpdate(LocalDateTime.now());
            indicatorService.addOrUpdateIndicator(indicator);
        }
    }
}
