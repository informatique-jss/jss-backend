package com.jss.osiris.modules.osiris.crm.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.crm.model.IKpiThread;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValue;

public interface KpiCrmService {
    public void computeKpis() throws OsirisException;

    List<KpiCrm> getKpiCrms();

    /**
     * Method called by batch to persist the KpiValues of a KpiCrm
     * 
     * @throws OsirisException
     */
    void computeKpiCrm(Integer kpiCrmId) throws OsirisException;

    KpiCrm getKpiCrm(Integer id);

    void saveValuesForKpiAndDay(KpiCrm kpiCrm, List<KpiCrmValue> values);

    KpiCrm getKpiCrmByCode(String code);

    IKpiThread getKpiThread(KpiCrm kpiCrm);
}
