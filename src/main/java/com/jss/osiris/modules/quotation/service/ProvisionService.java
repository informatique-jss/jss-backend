package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.ProvisionBoardDisplayedResult;


public interface ProvisionService {
    public Provision getProvision(Integer id);

    public void updateAssignedToForProvision(Provision provision, Employee employee);

    public List<ProvisionBoardDisplayedResult> getBoardALs(List<Integer> employees);

    public List<ProvisionBoardDisplayedResult> getBoardFormalite(List<Integer> employees);
}
