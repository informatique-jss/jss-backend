package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import com.jss.osiris.modules.miscellaneous.model.Department;
import com.jss.osiris.modules.miscellaneous.repository.DepartmentRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        if (!department.isEmpty())
            return department.get();
        return null;
    }
	
	 @Override
    public Department addOrUpdateDepartment(
            Department department) {
        return departmentRepository.save(department);
    }
}
