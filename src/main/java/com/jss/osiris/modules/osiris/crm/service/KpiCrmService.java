package com.jss.osiris.modules.osiris.crm.service;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.crm.dto.KpiWidgetDto;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;

public interface KpiCrmService {

    public KpiCrm getKpiCrmByCode(String code);

    public KpiCrm getKpiCrmById(Integer id);

    public void computeKpiCrm(Integer kpiCrmId);

    public List<KpiCrm> getKpiCrms();

    public String getKpiValues(Integer kpiCrmId, String timeScale, List<Integer> responsablesIds)
            throws JsonProcessingException;

    public void startComputeBatches() throws OsirisException;

    public List<KpiWidgetDto> getKpiCrmWidget(String displayedPageCode, String timescale, List<Integer> responsables);

    public List<KpiCrm> getKpiCrmsByDisplayedPageCode(String displayedPageCode);
}
