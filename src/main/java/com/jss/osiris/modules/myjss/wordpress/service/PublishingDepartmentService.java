package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;

import com.jss.osiris.modules.myjss.wordpress.model.PublishingDepartment;

public interface PublishingDepartmentService {
        public PublishingDepartment addOrUpdatePublishingDepartmentFromWordpress(
                        PublishingDepartment publishingDepartment);

        public List<PublishingDepartment> getAvailableDepartments();

        public PublishingDepartment getPublishingDepartment(Integer departmentId);
}
