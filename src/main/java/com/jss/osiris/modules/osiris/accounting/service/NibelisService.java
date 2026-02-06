package com.jss.osiris.modules.osiris.accounting.service;

import java.time.LocalDate;
import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.accounting.model.AccountingRecord;
import com.jss.osiris.modules.osiris.accounting.model.nibelis.PaySlip;

public interface NibelisService {
    public List<PaySlip> getAllPaySlips(LocalDate period) throws OsirisException;

    List<AccountingRecord> generateAccountingRecordForPayslip(LocalDate period, boolean persist) throws OsirisException;
}