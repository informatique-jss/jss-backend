package com.jss.osiris.modules.miscellaneous.repository;

import com.jss.osiris.modules.miscellaneous.model.ActiveDirectoryGroup;

import com.jss.osiris.libs.QueryCacheCrudRepository;

public interface ActiveDirectoryGroupRepository extends QueryCacheCrudRepository<ActiveDirectoryGroup, Integer> {

    ActiveDirectoryGroup findByCode(String code);
}