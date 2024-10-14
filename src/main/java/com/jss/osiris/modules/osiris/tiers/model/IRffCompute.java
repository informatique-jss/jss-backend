package com.jss.osiris.modules.osiris.tiers.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface IRffCompute {
    Integer getTiersId();

    Integer getResponsableId();

    Integer getRffId();

    BigDecimal getRffAl();

    BigDecimal getRffFor();

    LocalDate getStartDate();

    LocalDate getEndDate();
}