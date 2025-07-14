package com.jss.osiris.modules.myjss.wordpress.repository;

import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.myjss.wordpress.model.PublishingDepartment;

public interface PublishingDepartmentRepository extends QueryCacheCrudRepository<PublishingDepartment, Integer> {

    List<PublishingDepartment> findAllByOrderByCode();

    PublishingDepartment findByCode(String code);

}