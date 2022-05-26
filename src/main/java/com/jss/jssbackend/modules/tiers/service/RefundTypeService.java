package com.jss.jssbackend.modules.tiers.service;

import java.util.List;

import com.jss.jssbackend.modules.tiers.model.RefundType;

public interface RefundTypeService {
    public List<RefundType> getRefundTypes();

    public RefundType getRefundType(Integer id);
}
