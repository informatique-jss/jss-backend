package com.jss.osiris.modules.osiris.crm.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import com.jss.osiris.modules.osiris.tiers.model.Responsable;

public interface IKpiCrm {

    public String getCode();

    public String getLabel();

    public String getComputeType();

    public Map<LocalDate, BigDecimal> getComputeValue(Responsable responsable);
}
