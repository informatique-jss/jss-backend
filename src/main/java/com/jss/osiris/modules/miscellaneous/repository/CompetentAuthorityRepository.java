package com.jss.osiris.modules.miscellaneous.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;

public interface CompetentAuthorityRepository extends CrudRepository<CompetentAuthority, Integer> {

    List<CompetentAuthority> findByLabelContainingIgnoreCase(String label);

    List<CompetentAuthority> findByCity_Id(Integer cityId);

    Optional<CompetentAuthority> findByApiId(String apiId);

    List<CompetentAuthority> findByCompetentAuthorityType_Id(Integer competentAuthorityTypeId);
}