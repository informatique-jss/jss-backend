package com.jss.osiris.modules.tiers.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.tiers.model.IRffCompute;
import com.jss.osiris.modules.tiers.model.Rff;
import com.jss.osiris.modules.tiers.model.RffSearch;
import com.jss.osiris.modules.tiers.repository.RffRepository;

@Service
public class RffServiceImpl implements RffService {

    @Autowired
    RffRepository rffRepository;

    @Autowired
    ConstantService constantService;

    @Autowired
    ResponsableService responsableService;

    @Autowired
    TiersService tiersService;

    @Override
    public Rff getRff(Integer id) {
        Optional<Rff> rff = rffRepository.findById(id);
        if (rff.isPresent())
            return rff.get();
        return null;
    }

    @Override
    public List<Rff> getRffs(RffSearch rffSearch) throws OsirisException {
        Integer idTiers = 0;
        if (rffSearch.getTiers() != null)
            idTiers = rffSearch.getTiers().getEntityId();

        Integer idResponsable = 0;
        if (rffSearch.getResponsable() != null)
            idResponsable = rffSearch.getResponsable().getEntityId();

        Integer idSalesEmployee = 0;
        if (rffSearch.getSalesEmployee() != null)
            idSalesEmployee = rffSearch.getSalesEmployee().getId();

        List<IRffCompute> rffComputes = rffRepository.getRffComputes(constantService.getRffFrequencyAnnual().getId(),
                constantService.getRffFrequencyQuarterly().getId(), constantService.getRffFrequencyMonthly().getId(),
                idTiers, idResponsable, idSalesEmployee, rffSearch.getStartDate(), rffSearch.getEndDate());

        List<Rff> finalRffs = new ArrayList<Rff>();
        if (rffComputes != null)
            for (IRffCompute rffCompute : rffComputes) {
                Rff currentRff = new Rff();
                if (rffCompute.getRffId() != null) {
                    currentRff = getRff(rffCompute.getRffId());
                    if (currentRff.getIsCancelled() == false && currentRff.getIsSent() == false) {
                        currentRff.setRffFormalite(rffCompute.getRffFor());
                        currentRff.setRffInsertion(rffCompute.getRffAl());
                    }
                } else {
                    currentRff.setStartDate(rffCompute.getStartDate());
                    currentRff.setEndDate(rffCompute.getEndDate());
                    currentRff.setIsCancelled(false);
                    currentRff.setIsSent(false);
                    if (rffCompute.getResponsableId() != null)
                        currentRff.setResponsable(responsableService.getResponsable(rffCompute.getResponsableId()));
                    currentRff.setTiers(tiersService.getTiers(rffCompute.getTiersId()));
                    currentRff.setRffFormalite(rffCompute.getRffFor());
                    currentRff.setRffInsertion(rffCompute.getRffAl());
                    finalRffs.add(addOrUpdateRff(currentRff));
                }

                currentRff.setTiersId(currentRff.getTiers().getId());
                if (currentRff.getTiers().getDenomination() != null)
                    currentRff.setTiersLabel(currentRff.getTiers().getDenomination());
                else
                    currentRff.setTiersLabel(
                            currentRff.getTiers().getFirstname() + " " + currentRff.getTiers().getLastname());

                if (currentRff.getResponsable() != null) {
                    currentRff.setResponsableLabel(currentRff.getResponsable().getFirstname() + " "
                            + currentRff.getResponsable().getLastname());
                    currentRff.setResponsableId(currentRff.getResponsable().getId());
                }

                addOrUpdateRff(currentRff);
                if (!rffSearch.getIsHideCancelledRff() || currentRff.getIsCancelled() == false)
                    finalRffs.add(addOrUpdateRff(currentRff));
            }

        return finalRffs;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Rff addOrUpdateRff(Rff rff) {
        return rffRepository.save(rff);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Rff cancelRff(Rff rff) {
        rff = getRff(rff.getId());
        rff.setIsCancelled(true);
        return addOrUpdateRff(rff);
    }
}
