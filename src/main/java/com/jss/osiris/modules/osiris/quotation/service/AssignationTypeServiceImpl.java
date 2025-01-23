package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.quotation.model.AssignationType;
import com.jss.osiris.modules.osiris.quotation.repository.AssignationTypeRepository;

@Service
public class AssignationTypeServiceImpl implements AssignationTypeService {

    @Autowired
    AssignationTypeRepository assignationTypeRepository;

    @Override
    public List<AssignationType> getAssignationTypes() {
        return IterableUtils.toList(assignationTypeRepository.findAll());
    }

    @Override
    public AssignationType getAssignationType(Integer id) {
        Optional<AssignationType> assignationType = assignationTypeRepository.findById(id);
        if (assignationType.isPresent())
            return assignationType.get();
        return null;
    }

    @Override
    public AssignationType addOrUpdateAssignationType(
            AssignationType assignationType) {
        return assignationTypeRepository.save(assignationType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAssignationTypes() {
        updateAssignationType(AssignationType.FORMALISTE, "Assigner au formaliste");
        updateAssignationType(AssignationType.EMPLOYEE, "Assigné à l'employé par défaut");
        updateAssignationType(AssignationType.PUBLICISTE, "Assigner au publiciste");
    }

    private void updateAssignationType(String code, String label) {
        if (getAssignationTypeByCode(code) == null) {
            AssignationType assignationType = new AssignationType();
            assignationType.setCode(code);
            assignationType.setLabel(label);
            assignationTypeRepository.save(assignationType);
        }
    }

    private Object getAssignationTypeByCode(String code) {
        return assignationTypeRepository.findByCode(code);
    }
}
