package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.miscellaneous.model.Department;
import com.jss.osiris.modules.miscellaneous.repository.DepartmentRepository;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    DepartmentRepository departmentRepository;

    @Override
    @Cacheable(value = "departmentList", key = "#root.methodName")
    public List<Department> getDepartments() {
        return IterableUtils.toList(departmentRepository.findAll());
    }

    @Override
    @Cacheable(value = "department", key = "#id")
    public Department getDepartment(Integer id) {
        Optional<Department> department = departmentRepository.findById(id);
        if (department.isPresent())
            return department.get();
        return null;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "departmentList", allEntries = true),
            @CacheEvict(value = "department", key = "#department.id")
    })
    @Transactional(rollbackFor = Exception.class)
    public Department addOrUpdateDepartment(
            Department department) {
        return departmentRepository.save(department);
    }
}
