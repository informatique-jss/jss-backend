package com.jss.osiris.modules.osiris.accounting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jss.osiris.modules.osiris.accounting.model.nibelis.NibelisEmployee;
import com.jss.osiris.modules.osiris.accounting.model.nibelis.PaySlip;

public interface PaySlipRepository extends JpaRepository<PaySlip, Integer> {

    List<PaySlip> findByEmployeeAndMonth(NibelisEmployee salarie, String string);
}