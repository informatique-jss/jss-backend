package com.jss.osiris.modules.tiers.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.tiers.model.Rff;
import com.jss.osiris.modules.tiers.model.RffSearch;

public interface RffService {

    public Rff getRff(Integer id);

    public List<Rff> getRffs(RffSearch rffSearch) throws OsirisException;

    public Rff addOrUpdateRff(Rff rff);

    public Rff cancelRff(Rff rff);
}
