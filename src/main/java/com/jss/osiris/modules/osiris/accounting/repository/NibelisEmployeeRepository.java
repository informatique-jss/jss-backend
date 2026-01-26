package com.jss.osiris.modules.osiris.accounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jss.osiris.modules.osiris.accounting.model.nibelis.NibelisEmployee;

public interface NibelisEmployeeRepository extends JpaRepository<NibelisEmployee, Integer> {
}