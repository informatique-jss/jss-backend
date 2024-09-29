package com.jss.osiris.modules.wordpress.service;

import java.util.List;

import com.jss.osiris.modules.wordpress.model.PublishingDepartment;

public interface PublishingDepartmentService {
        public List<PublishingDepartment> getAvailableDepartments();
}
