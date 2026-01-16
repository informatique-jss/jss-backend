package com.jss.osiris.modules.osiris.tiers.facade;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.ResponsableSearch;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;
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
    public Page<TiersDto> searchTiersByDenoOrFirstLastName(String searchText, Pageable pageable)
            throws OsirisException {
        tiersService.findAllTiersByDenoOrFirstLastName(searchText, pageable);

        return tiersDtoHelper
                .mapTiers(tiersService.findAllTiersByDenoOrFirstLastName(searchText, pageable));
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

    public List<ResponsableDto> getResponsablesByTiers(Tiers tiers) {
        List<Responsable> respos = responsableService.getResponsablesByTiers(tiers);

        return tiersDtoHelper.mapResponsables(respos);
    }

    public ResponsableDto getResponsableDto(Integer id) {
        Responsable responsable = responsableService.getResponsable(id);
        if (responsable != null)
            return tiersDtoHelper.mapResponsable(responsable);

        return null;
    }

    public Page<ResponsableDto> searchResponsablesByFirstOrLastName(String searchText, Pageable pageable) {
        return tiersDtoHelper.mapResponsables(responsableService.getResponsables(searchText, pageable));
    }
}