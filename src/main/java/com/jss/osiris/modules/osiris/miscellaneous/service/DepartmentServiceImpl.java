package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.miscellaneous.model.Department;
import com.jss.osiris.modules.osiris.miscellaneous.repository.DepartmentRepository;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    DepartmentRepository departmentRepository;

    @Override
    public List<Department> getDepartments() {
        return IterableUtils.toList(departmentRepository.findAll());
    }

    @Override
    public Department getDepartment(Integer id) {
        Optional<Department> department = departmentRepository.findById(id);
        if (department.isPresent())
            return department.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Department addOrUpdateDepartment(
            Department department) {
        return departmentRepository.save(department);
    }
}
