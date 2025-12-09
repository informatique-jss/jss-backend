package com.jss.osiris.modules.osiris.crm.service.kpi;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.crm.model.IKpiThread;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmCategory;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValue;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmService;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmValueService;
import com.jss.osiris.modules.osiris.profile.model.DailyConnexion;
import com.jss.osiris.modules.osiris.profile.service.DailyConnexionService;
import com.jss.osiris.modules.osiris.reporting.model.ReportingWidget;

@Component
public class KpiMyJssConnexion implements IKpiThread {

    @Autowired
    KpiCrmValueService kpiCrmValueService;

    @Autowired
    KpiCrmService kpiCrmService;

    @Autowired
    DailyConnexionService dailyConnexionService;

    @Override
    public String getCode() {
        return "MYJSS_CONNEXION";
    }

    @Override
    public BigDecimal getDefaultValue() {
        return new BigDecimal(0);
    }

    @Override
    public void computeKpiCrmValues() throws OsirisException {
        KpiCrm kpiCrm = kpiCrmService.getKpiCrmByCode(getCode());
        if (kpiCrm == null)
            throw new OsirisException("KpiCrm not defined for code " + getCode());

        LocalDate lastDate = kpiCrmValueService.getLastKpiCrmValueDate(kpiCrm);

        while (lastDate.isBefore(LocalDate.now())) {
            List<KpiCrmValue> newValues = new ArrayList<KpiCrmValue>();

            List<DailyConnexion> connexions = dailyConnexionService.getDailyConnexionsForDay(lastDate);

            if (connexions != null)
                for (DailyConnexion connexion : connexions) {
                    KpiCrmValue value = new KpiCrmValue();
                    value.setKpiCrm(kpiCrm);
                    value.setResponsable(connexion.getResponsable());
                    value.setValue(new BigDecimal(1));
                    value.setValueDate(lastDate);
                    newValues.add(value);
                }

            if (newValues != null && newValues.size() > 0) {
                kpiCrmService.saveValuesForKpiAndDay(kpiCrm, newValues);
            }

            lastDate = lastDate.plusDays(1);
        }
    }

    @Override
    public String getAggregateTypeForResponsable() {
        return KpiCrm.AGGREGATE_TYPE_SUM;
    }

    @Override
    public String getAggregateTypeForTimePeriod() {
        return KpiCrm.AGGREGATE_TYPE_SUM;
    }

    @Override
    public String getLabelType() {
        return ReportingWidget.LABEL_TYPE_DATETIME;
    }

    @Override
    public String getUnit() {
        return null;
    }

    @Override
    public String getIcon() {
        return "tablerKey";
    }

    @Override
    public Boolean getIsPositiveEvolutionGood() {
        return true;
    }

    @Override
    public String getGraphType() {
        return KpiCrm.GRAPH_TYPE_BAR;
    }

    @Override
    public String getKpiCrmCategoryCode() {
        return KpiCrmCategory.WEBSITE;
    }

    @Override
    public Integer getDisplayOrder() {
        return null;
    }
}
