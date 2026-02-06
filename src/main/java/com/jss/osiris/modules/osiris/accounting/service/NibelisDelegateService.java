package com.jss.osiris.modules.osiris.accounting.service;

import java.time.LocalDate;
import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.accounting.model.nibelis.BulletinLigne;
import com.jss.osiris.modules.osiris.accounting.model.nibelis.Salarie;

public interface NibelisDelegateService {
    List<Salarie> getSalaries() throws OsirisException;

    List<BulletinLigne> getBulletin(Long matricule, LocalDate period) throws OsirisException;
}