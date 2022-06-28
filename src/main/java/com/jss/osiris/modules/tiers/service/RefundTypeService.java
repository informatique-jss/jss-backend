package com.jss.osiris.modules.tiers.service;

import java.util.List;

import com.jss.osiris.modules.tiers.model.RefundType;

public interface RefundTypeService {
    public List<RefundType> getRefundTypes();

    public RefundType getRefundType(Integer id);
	
	 public RefundType addOrUpdateRefundType(RefundType refundType);
}
