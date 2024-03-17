package com.jss.osiris.libs.batch.model;

import java.time.LocalDateTime;

public interface IBatchTimeStatistics {
    LocalDateTime getDateTime();

    Integer getNbr();

    Boolean getHasError();

    Float getMeanTime();
}