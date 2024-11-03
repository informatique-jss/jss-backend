package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.myjss.wordpress.model.PublishingDepartment;
import com.jss.osiris.modules.myjss.wordpress.repository.PublishingDepartmentRepository;

@Service
public class PublishingDepartmentServiceImpl implements PublishingDepartmentService {

    @Autowired
    PublishingDepartmentRepository publishingDepartmentRepository;

    @Override
    public PublishingDepartment getPublishingDepartment(Integer id) {
        Optional<PublishingDepartment> department = publishingDepartmentRepository.findById(id);
        if (department.isPresent())
            return department.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PublishingDepartment addOrUpdatePublishingDepartmentFromWordpress(
            PublishingDepartment publishingDepartment) {
        if (publishingDepartment.getAcf() != null)
            publishingDepartment.setCode(publishingDepartment.getAcf().getCode());
        return publishingDepartmentRepository.save(publishingDepartment);
    }

    @Override
    public List<PublishingDepartment> getAvailableDepartments() {
        return publishingDepartmentRepository.findAllByOrderByCode();
    }
}
