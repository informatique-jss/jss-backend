package com.jss.osiris.modules.osiris.tiers.service;

import java.util.List;

import com.jss.osiris.modules.osiris.tiers.model.RefundType;

public interface RefundTypeService {
    public List<RefundType> getRefundTypes();

    public RefundType getRefundType(Integer id);

    public RefundType addOrUpdateRefundType(RefundType refundType);
}
