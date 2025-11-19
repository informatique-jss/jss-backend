package com.jss.osiris.modules.osiris.tiers.facade;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;
import com.jss.osiris.modules.osiris.tiers.model.dto.ResponsableDto;
import com.jss.osiris.modules.osiris.tiers.model.dto.TiersDto;

@Service
public class TiersDtoHelper {
    public List<TiersDto> mapTiers(List<Tiers> tiers) {
        List<TiersDto> outTiers = new ArrayList<TiersDto>();

        if (tiers != null) {
            for (Tiers tier : tiers) {
                TiersDto outTier = new TiersDto();
                outTier.setDenomination(tier.getDenomination());
                outTier.setId(tier.getId());
                outTier.setKpiValues(tier.getKpiValues());
                outTiers.add(outTier);
            }
        }
        return outTiers;
    }

    public List<ResponsableDto> mapResponsable(List<Responsable> responsables) {
        List<ResponsableDto> outTiers = new ArrayList<ResponsableDto>();

        if (responsables != null) {
            for (Responsable responsable : responsables) {
                ResponsableDto outTier = new ResponsableDto();
                outTier.setFirstname(responsable.getFirstname());
                outTier.setLastname(responsable.getLastname());
                outTier.setId(responsable.getId());
                outTier.setKpiValues(responsable.getKpiValues());
                outTiers.add(outTier);
            }
        }
        return outTiers;
    }
}
