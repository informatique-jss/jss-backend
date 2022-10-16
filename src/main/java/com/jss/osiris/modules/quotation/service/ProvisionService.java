package com.jss.osiris.modules.quotation.service;

import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.quotation.model.Provision;

public interface ProvisionService {
    public Provision getProvision(Integer id);

    public void updateAssignedToForProvision(Provision provision, Employee employee);
}
