package com.jss.osiris.modules.invoicing.service;

import java.util.List;

import com.jss.osiris.modules.invoicing.model.Refund;

public interface RefundService {
    public List<Refund> getRefunds();

    public Refund getRefund(Integer id);
	
	 public Refund addOrUpdateRefund(Refund refund);
}
