package com.jss.osiris.modules.osiris.invoicing.service;

import java.time.LocalDateTime;
import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.invoicing.model.GuMatchingResultDto;

public interface GuMatchingService {
    public List<GuMatchingResultDto> getInpiMatchingResult(LocalDateTime starDate,
            LocalDateTime endDate) throws OsirisException;

}
