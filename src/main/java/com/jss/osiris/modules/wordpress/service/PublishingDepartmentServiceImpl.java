package com.jss.osiris.modules.wordpress.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.wordpress.model.PublishingDepartment;

@Service
public class PublishingDepartmentServiceImpl implements PublishingDepartmentService {

    @Autowired
    WordpressDelegate wordpressDelegate;

    @Override
    @Cacheable(value = "wordpress-departments")
    public List<PublishingDepartment> getAvailableDepartments() {
        List<PublishingDepartment> departments = wordpressDelegate.getAvailableDepartments();
        if (departments != null && departments.size() > 0) {
            for (PublishingDepartment department : departments)
                if (department.getAcf() != null)
                    department.setCode(department.getAcf().getCode());

            departments.sort(new Comparator<PublishingDepartment>() {
                @Override
                public int compare(PublishingDepartment o1, PublishingDepartment o2) {
                    return o1.getCode().compareTo(o2.getCode());
                }
            });
        }
        return departments;
    }
}
