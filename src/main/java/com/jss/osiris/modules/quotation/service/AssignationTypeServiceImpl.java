package com.jss.osiris.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.AssignationType;
import com.jss.osiris.modules.quotation.repository.AssignationTypeRepository;

@Service
public class AssignationTypeServiceImpl implements AssignationTypeService {

    @Autowired
    AssignationTypeRepository assignationTypeRepository;

    @Override
    @Cacheable(value = "assignationTypeList", key = "#root.methodName")
    public List<AssignationType> getAssignationTypes() {
        return IterableUtils.toList(assignationTypeRepository.findAll());
    }

    @Override
    @Cacheable(value = "assignationType", key = "#id")
    public AssignationType getAssignationType(Integer id) {
        Optional<AssignationType> assignationType = assignationTypeRepository.findById(id);
        if (assignationType.isPresent())
            return assignationType.get();
        return null;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "assignationTypeList", allEntries = true),
            @CacheEvict(value = "assignationType", key = "#assignationType.id")
    })
    public AssignationType addOrUpdateAssignationType(
            AssignationType assignationType) {
        return assignationTypeRepository.save(assignationType);
    }

    @Override
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
