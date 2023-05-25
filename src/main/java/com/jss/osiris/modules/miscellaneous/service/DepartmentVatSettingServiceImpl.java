package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.miscellaneous.model.Department;
import com.jss.osiris.modules.miscellaneous.model.DepartmentVatSetting;
import com.jss.osiris.modules.miscellaneous.repository.DepartmentVatSettingRepository;

@Service
public class DepartmentVatSettingServiceImpl implements DepartmentVatSettingService {

    @Autowired
    DepartmentVatSettingRepository departmentVatSettingRepository;

    @Override
    public List<DepartmentVatSetting> getDepartmentVatSettings() {
        return IterableUtils.toList(departmentVatSettingRepository.findAll());
    }

    @Override
    public DepartmentVatSetting getDepartmentVatSetting(Integer id) {
        Optional<DepartmentVatSetting> departmentVatSetting = departmentVatSettingRepository.findById(id);
        if (departmentVatSetting.isPresent())
            return departmentVatSetting.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DepartmentVatSetting addOrUpdateDepartmentVatSetting(
            DepartmentVatSetting departmentVatSetting) {
        return departmentVatSettingRepository.save(departmentVatSetting);
    }

    @Override
    public DepartmentVatSetting getDepartmentVatSettingByDepartment(Department department) {
        return departmentVatSettingRepository.findByDepartment(department);
    }
}
