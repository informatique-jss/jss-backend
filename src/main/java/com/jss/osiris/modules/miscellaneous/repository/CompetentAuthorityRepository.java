package com.jss.osiris.modules.miscellaneous.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;

public interface CompetentAuthorityRepository extends CrudRepository<CompetentAuthority, Integer> {

    List<CompetentAuthority> findByLabelContainingIgnoreCase(String label);

}