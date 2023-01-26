package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.ProvisionBoardResult;
import com.jss.osiris.modules.quotation.model.ProvisionStatus;


public interface ProvisionService {
    public Provision getProvision(Integer id);

    public void updateAssignedToForProvision(Provision provision, Employee employee);

    public List<List<ProvisionStatus>> getBoardAnnouncementStatus();
    
    public List<ProvisionBoardResult> getBoardALs(List<Integer> employees);

    public List<ProvisionBoardResult> getBoardFormalite(List<Integer> employees);
}
