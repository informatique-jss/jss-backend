package com.jss.osiris.modules.osiris.quotation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.quotation.model.ServiceFamily;
import com.jss.osiris.modules.osiris.quotation.model.ServiceFamilyGroup;

public interface ServiceFamilyRepository extends QueryCacheCrudRepository<ServiceFamily, Integer> {

    List<ServiceFamily> findByServiceFamilyGroup(ServiceFamilyGroup serviceFamilyGroup);

    @Query("select s from ServiceFamily s where s.serviceFamilyGroup != :serviceFamilyGroupAnnouncement")
    List<ServiceFamily> findServiceFamiliesExcludingServiceFamilyGroupAnnouncement(
            @Param("serviceFamilyGroupAnnouncement") ServiceFamilyGroup serviceFamilyGroupAnnouncement);
}