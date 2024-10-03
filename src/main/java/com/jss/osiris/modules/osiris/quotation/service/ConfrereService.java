package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.model.Department;
import com.jss.osiris.modules.osiris.quotation.model.Confrere;

public interface ConfrereService {
    public List<Confrere> getConfreres();

    public Confrere getConfrere(Integer id);

    public Confrere addOrUpdateConfrere(Confrere confrere) throws OsirisException;

    public List<Confrere> searchConfrereFilteredByDepartmentAndName(Department department, String label);
}
