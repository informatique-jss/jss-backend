package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.RejectionCause;

public interface RejectionCauseService {
    public List<RejectionCause> getRejectionCauses();

    public RejectionCause getRejectionCause(Integer id);

    public RejectionCause addOrUpdateRejectionCause(RejectionCause rejectionCause);
}
