package com.jss.osiris.modules.quotation.service;

import java.io.File;
import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.ProvisionBoardResult;

public interface ProvisionService {
    public Provision getProvision(Integer id);

    public void updateAssignedToForProvision(Provision provision, Employee employee);

    public List<ProvisionBoardResult> getDashboardEmployee(List<Employee> employees);

    public Provision addOrUpdateProvision(Provision provision);

    public File getRegistrationActPdf(Provision provision) throws OsirisException;

    public File getTrackingSheetPdf(Provision provision) throws OsirisException;
}
