package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.DebourDel;
import com.jss.osiris.modules.osiris.quotation.model.Provision;

public interface DebourDelService {
    public List<DebourDel> getDebourByProvision(Provision provision);
}
