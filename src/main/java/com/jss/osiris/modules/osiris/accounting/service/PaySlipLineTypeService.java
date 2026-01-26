package com.jss.osiris.modules.osiris.accounting.service;

import java.util.List;

import com.jss.osiris.modules.osiris.accounting.model.PaySlipLineType;

public interface PaySlipLineTypeService {
    public List<PaySlipLineType> getPaySlipLineTypes();

    public PaySlipLineType getPaySlipLineType(Integer id);

    public PaySlipLineType addOrUpdatePaySlipLineType(PaySlipLineType paySlipLineType);

    public PaySlipLineType getPaySlipLineTypeByLabel(String label);
}
