package com.jss.osiris.modules.osiris.invoicing.service;

import java.time.LocalDate;
import java.util.List;

import com.jss.osiris.modules.osiris.invoicing.model.GuMatchingResultDto;

public interface GuMatchingService {
    public List<GuMatchingResultDto> getInpiMatchingResult(LocalDate starDate,
            LocalDate endDate);

}
