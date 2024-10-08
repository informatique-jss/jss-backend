package com.jss.osiris.modules.osiris.tiers.model;

import java.time.LocalDate;

public interface IRffCompute {
    Integer getTiersId();

    Integer getResponsableId();

    Integer getRffId();

    Double getRffAl();

    Double getRffFor();

    LocalDate getStartDate();

    LocalDate getEndDate();
}