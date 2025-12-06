package com.jss.osiris.modules.osiris.tiers.facade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;
import com.jss.osiris.modules.osiris.tiers.model.dto.ResponsableDto;
import com.jss.osiris.modules.osiris.tiers.model.dto.TiersDto;

@Service
public class TiersDtoHelper {

    @Autowired
    KpiCrmService kpiCrmService;

    public List<TiersDto> mapTiers(List<Tiers> tiers) {
        List<TiersDto> outTiers = new ArrayList<TiersDto>();
        List<KpiCrm> kpiCrms = kpiCrmService.getKpiCrms();
        HashMap<String, KpiCrm> kpiCrmsMap = new HashMap<>();
        for (KpiCrm kpiCrm : kpiCrms) {
            kpiCrmsMap.put(kpiCrm.getLabel(), kpiCrm);
        }

        if (tiers != null) {
            for (Tiers tier : tiers) {
                TiersDto outTier = new TiersDto();
                outTier.setDenomination(tier.getDenomination() != null ? tier.getDenomination()
                        : (tier.getFirstname() + " " + tier.getLastname()));
                outTier.setId(tier.getId());
                outTier.setKpiValues(new HashMap<String, String>());
                if (tier.getKpiValues() != null)
                    for (String kpiLabel : tier.getKpiValues().keySet()) {
                        outTier.getKpiValues().put(kpiLabel,
                                getFormatedAggregatedValue(kpiCrmsMap.get(kpiLabel),
                                        tier.getKpiValues().get(kpiLabel)));
                    }
                if (tier.getSalesEmployee() != null)
                    outTier.setSalesEmployee(
                            tier.getSalesEmployee().getFirstname() + " " + tier.getSalesEmployee().getLastname());
                outTier.setFormalisteEmployee(
                        tier.getFormalisteEmployee().getFirstname() + " " + tier.getFormalisteEmployee().getLastname());
                outTier.setIsNewTiers(Boolean.TRUE.equals(tier.getIsNewTiers()) ? "Oui" : "Non");
                outTier.setAddress(tier.getAddress() + " " + (tier.getCity() != null ? tier.getCity().getLabel() : ""));
                outTier.setTiersCategory(tier.getTiersCategory() != null ? tier.getTiersCategory().getLabel() : null);
                outTiers.add(outTier);
            }
        }
        return outTiers;
    }

    public List<ResponsableDto> mapResponsable(List<Responsable> responsables) {
        List<ResponsableDto> outTiers = new ArrayList<ResponsableDto>();
        List<KpiCrm> kpiCrms = kpiCrmService.getKpiCrms();
        HashMap<String, KpiCrm> kpiCrmsMap = new HashMap<>();
        for (KpiCrm kpiCrm : kpiCrms) {
            kpiCrmsMap.put(kpiCrm.getLabel(), kpiCrm);
        }

        if (responsables != null) {
            for (Responsable responsable : responsables) {
                ResponsableDto outResponsable = new ResponsableDto();
                outResponsable.setFirstname(responsable.getFirstname());
                outResponsable.setLastname(responsable.getLastname());
                outResponsable.setId(responsable.getId());

                outResponsable.setKpiValues(new HashMap<String, String>());
                if (responsable.getKpiValues() != null)
                    for (String kpiLabel : responsable.getKpiValues().keySet()) {
                        outResponsable.getKpiValues().put(kpiLabel,
                                getFormatedAggregatedValue(kpiCrmsMap.get(kpiLabel),
                                        responsable.getKpiValues().get(kpiLabel)));
                    }

                outResponsable.setTiersId(responsable.getTiers().getId());
                outResponsable.setTiersDenomination(
                        responsable.getTiers().getDenomination() != null ? responsable.getTiers().getDenomination()
                                : (responsable.getTiers().getFirstname() + " " + responsable.getTiers().getLastname()));
                outResponsable.setTiersCategory(responsable.getTiers().getTiersCategory() != null
                        ? responsable.getTiers().getTiersCategory().getLabel()
                        : null);
                outResponsable.setResponsableCategory(
                        responsable.getTiersCategory() != null ? responsable.getTiersCategory().getLabel() : null);
                if (responsable.getSalesEmployee() != null)
                    outResponsable.setSalesEmployee(responsable.getSalesEmployee().getFirstname() + " "
                            + responsable.getSalesEmployee().getLastname());
                outTiers.add(outResponsable);
            }
        }
        return outTiers;
    }

    private String getFormatedAggregatedValue(KpiCrm kpiCrm, BigDecimal value) {
        if (kpiCrm.getUnit() != null && kpiCrm.getUnit().equals("€"))
            return String.format("%,.2f €", value);
        return value.setScale(2, RoundingMode.HALF_EVEN).toString();
    }
}
