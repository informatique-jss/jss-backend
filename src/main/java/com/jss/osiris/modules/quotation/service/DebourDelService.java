package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.quotation.model.DebourDel;
import com.jss.osiris.modules.quotation.model.Provision;

public interface DebourDelService {
    public List<DebourDel> getDebourByProvision(Provision provision);
}
