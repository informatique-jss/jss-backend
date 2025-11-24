package com.jss.osiris.modules.osiris.crm.service.kpi;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.crm.model.IKpiThread;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValue;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmService;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmValueService;
import com.jss.osiris.modules.osiris.miscellaneous.model.CustomerOrderOrigin;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.reporting.model.ReportingWidget;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class KpiWebsiteOrderCreation implements IKpiThread {

    @Autowired
    KpiCrmValueService kpiCrmValueService;

    @Autowired
    KpiCrmService kpiCrmService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    ConstantService constantService;

    @PersistenceContext
    EntityManager em;

    @Override
    public String getCode() {
        return "WEBSITE_ORDER_CREATION";
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

            List<CustomerOrder> orders = customerOrderService.getByCreatedDateBetweenAndStatus(lastDate.atStartOfDay(),
                    lastDate.atTime(23, 59, 59),
                    null);

            if (orders != null) {
                orders.sort(new Comparator<CustomerOrder>() {
                    @Override
                    public int compare(CustomerOrder firstOrder, CustomerOrder secondOrder) {
                        if (firstOrder.getResponsable() != null && secondOrder.getResponsable() == null)
                            return 1;
                        if (firstOrder.getResponsable() == null && secondOrder.getResponsable() != null)
                            return -1;
                        if (firstOrder.getResponsable() == null && secondOrder.getResponsable() == null)
                            return 0;
                        if (firstOrder.getResponsable().getId() > secondOrder.getResponsable().getId())
                            return 1;
                        if (firstOrder.getResponsable().getId() < secondOrder.getResponsable().getId())
                            return -1;
                        return 0;
                    }
                });

                Responsable currentResponsable = null;
                Integer nbrOrder = 0;
                CustomerOrderOrigin originOsiris = constantService.getCustomerOrderOriginOsiris();

                for (CustomerOrder order : orders) {
                    if (!order.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.DRAFT)
                            && order.getResponsable() != null && order.getCustomerOrderOrigin() != null
                            && !order.getCustomerOrderOrigin().getId().equals(originOsiris.getId())) {
                        if (currentResponsable == null
                                || !order.getResponsable().getId().equals(currentResponsable.getId())) {
                            if (!(new BigDecimal(nbrOrder)).equals(getDefaultValue())) {
                                KpiCrmValue value = new KpiCrmValue();
                                value.setKpiCrm(kpiCrm);
                                value.setResponsable(currentResponsable);
                                value.setValue(new BigDecimal(nbrOrder));
                                value.setValueDate(lastDate);
                                newValues.add(value);
                            }
                            currentResponsable = order.getResponsable();
                            nbrOrder = 0;
                        }
                        nbrOrder++;
                    }
                }

                if (newValues != null && newValues.size() > 0) {
                    kpiCrmService.saveValuesForKpiAndDay(kpiCrm, newValues);
                    em.flush();
                    em.clear();
                }
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
        return "tablerShoppingBagSearch";
    }

    @Override
    public Boolean getIsPositiveEvolutionGood() {
        return true;
    }

    @Override
    public String getGraphType() {
        return KpiCrm.GRAPH_TYPE_BAR;
    }

}
