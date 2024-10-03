package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.AssignationType;

public interface AssignationTypeService {
    public List<AssignationType> getAssignationTypes();

    public AssignationType getAssignationType(Integer id);

    public AssignationType addOrUpdateAssignationType(AssignationType assignationType);

    public void updateAssignationTypes();
}
