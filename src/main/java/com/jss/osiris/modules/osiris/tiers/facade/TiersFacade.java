package com.jss.osiris.modules.osiris.tiers.facade;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.tiers.model.ResponsableSearch;
import com.jss.osiris.modules.osiris.tiers.model.TiersSearch;
import com.jss.osiris.modules.osiris.tiers.model.dto.ResponsableDto;
import com.jss.osiris.modules.osiris.tiers.model.dto.TiersDto;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;
import com.jss.osiris.modules.osiris.tiers.service.TiersService;

@Service
public class TiersFacade {

    @Autowired
    TiersService tiersService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    TiersDtoHelper tiersDtoHelper;

    @Autowired
    ResponsableService responsableService;

    @Transactional(rollbackFor = Exception.class)
    public List<TiersDto> searchTiers(TiersSearch tiersSearch) throws OsirisException {
        if (tiersSearch.getSalesEmployee() != null)
            tiersSearch.setSalesEmployee(employeeService.getEmployee(tiersSearch.getSalesEmployee().getId()));

        return tiersDtoHelper.mapTiers(tiersService.searchForTiers(tiersSearch));
    }

    @Transactional(rollbackFor = Exception.class)
    public TiersDto getTiersDtoByTiersId(Integer idTiers) throws OsirisException {
        return tiersDtoHelper.mapTiers(tiersService.getTiers(idTiers));
    }

    public List<ResponsableDto> searchResponsable(ResponsableSearch responsableSearch) throws OsirisException {
        if (responsableSearch.getSalesEmployee() != null)
            responsableSearch
                    .setSalesEmployee(employeeService.getEmployee(responsableSearch.getSalesEmployee().getId()));

        return tiersDtoHelper.mapResponsables(responsableService.searchForResponsable(responsableSearch));
    }
}
