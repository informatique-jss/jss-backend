package com.jss.osiris.modules.osiris.tiers.facade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.accounting.service.AccountingAccountHelper;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmService;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;
import com.jss.osiris.modules.osiris.miscellaneous.model.Phone;
import com.jss.osiris.modules.osiris.tiers.model.Competitor;
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
        HashMap<String, KpiCrm> kpiCrmsMap = getKpiCrmsMap();
        if (tiers != null) {
            for (Tiers tier : tiers) {
                outTiers.add(mapTiersToTiersDto(tier, kpiCrmsMap));
            }
        }
        return outTiers;
    }

    public TiersDto mapTiers(Tiers tiers) {
        TiersDto outTiers = new TiersDto();
        HashMap<String, KpiCrm> kpiCrmsMap = getKpiCrmsMap();
        if (tiers != null) {
            outTiers = mapTiersToTiersDto(tiers, kpiCrmsMap);
            mapTiersDetailsToTiersDto(outTiers, tiers);
        }
        return outTiers;
    }

    public List<ResponsableDto> mapResponsables(List<Responsable> responsables) {
        List<ResponsableDto> outResponsables = new ArrayList<ResponsableDto>();
        HashMap<String, KpiCrm> kpiCrmsMap = getKpiCrmsMap();

        if (responsables != null) {
            for (Responsable responsable : responsables) {
                outResponsables.add(mapResponsableToResponsableDto(responsable, kpiCrmsMap));
            }
        }
        return outResponsables;
    }

    public ResponsableDto mapResponsable(Responsable responsable) {
        ResponsableDto outResponsable = new ResponsableDto();
        HashMap<String, KpiCrm> kpiCrmsMap = getKpiCrmsMap();

        if (responsable != null) {
            outResponsable = mapResponsableToResponsableDto(responsable, kpiCrmsMap);
            mapResponsableDetailsToResponsableDto(outResponsable, responsable);
        }
        return outResponsable;
    }

    private TiersDto mapTiersToTiersDto(Tiers tier, HashMap<String, KpiCrm> kpiCrmsMap) {
        TiersDto tiersDto = new TiersDto();
        tiersDto.setDenomination(tier.getDenomination() != null ? tier.getDenomination()
                : (tier.getFirstname() + " " + tier.getLastname()));
        tiersDto.setId(tier.getId());

        if (kpiCrmsMap == null) {
            List<KpiCrm> kpiCrms = kpiCrmService.getKpiCrms();
            kpiCrmsMap = new HashMap<>();
            for (KpiCrm kpiCrm : kpiCrms) {
                kpiCrmsMap.put(kpiCrm.getLabel(), kpiCrm);
            }
        }
        tiersDto.setKpiValues(new HashMap<String, String>());
        if (tier.getKpiValues() != null)
            for (String kpiLabel : tier.getKpiValues().keySet()) {
                tiersDto.getKpiValues().put(kpiLabel,
                        getFormatedAggregatedValue(kpiCrmsMap.get(kpiLabel),
                                tier.getKpiValues().get(kpiLabel)));
            }

        if (tier.getSalesEmployee() != null)
            tiersDto.setSalesEmployee(
                    tier.getSalesEmployee().getFirstname() + " " + tier.getSalesEmployee().getLastname());
        if (tier.getFormalisteEmployee() != null)
            tiersDto.setFormalisteEmployee(
                    tier.getFormalisteEmployee().getFirstname() + " " + tier.getFormalisteEmployee().getLastname());
        tiersDto.setIsNewTiers(tier.getIsNewTiers() != null ? tier.getIsNewTiers() : Boolean.FALSE);
        tiersDto.setTiersCategory(tier.getTiersCategory() != null ? tier.getTiersCategory().getLabel() : null);
        tiersDto.setTiersType(tier.getTiersType() != null ? tier.getTiersType().getLabel() : null);
        tiersDto.setAddress(tier.getAddress());
        tiersDto.setPostalCode(tier.getPostalCode() != null ? tier.getPostalCode() : null);
        tiersDto.setCedexComplement(tier.getCedexComplement() != null ? tier.getCedexComplement() : null);
        tiersDto.setCity(tier.getCity() != null ? tier.getCity() : null);
        tiersDto.setCountry(tier.getCountry() != null ? tier.getCountry() : null);
        return tiersDto;
    }

    // Details of the tiers
    private TiersDto mapTiersDetailsToTiersDto(TiersDto tiersDto, Tiers tiers) {
        tiersDto.setPhones(
                tiers.getPhones() != null ? tiers.getPhones().stream().map(Phone::getPhoneNumber).toList() : null);
        tiersDto.setMails(tiers.getMails() != null ? tiers.getMails().stream().map(Mail::getMail).toList() : null);
        tiersDto.setMailRecipient(tiers.getMailRecipient() != null ? tiers.getMailRecipient() : null);
        tiersDto.setRffFormaliteRate(tiers.getRffFormaliteRate() != null ? tiers.getRffFormaliteRate() : null);
        tiersDto.setRffInsertionRate(tiers.getRffInsertionRate() != null ? tiers.getRffInsertionRate() : null);
        tiersDto.setSpecialOffers(
                tiers.getSpecialOffers() != null
                        ? tiers.getSpecialOffers().stream()
                                .map((offer) -> offer.getCustomLabel() != null ? offer.getCustomLabel()
                                        : offer.getLabel())
                                .toList()
                        : null);

        tiersDto.setInstructions(tiers.getInstructions());
        tiersDto.setObservations(tiers.getObservations());
        tiersDto.setCompetitors(
                tiers.getCompetitors() != null ? tiers.getCompetitors().stream().map(Competitor::getLabel).toList()
                        : null);

        if (tiers.getAccountingAccountCustomer() != null
                && tiers.getAccountingAccountCustomer().getPrincipalAccountingAccount() != null) {
            tiersDto.setAccountingAccountCustomer(tiers.getAccountingAccountCustomer().getLabel() + " - " +
                    AccountingAccountHelper.computeAccountingAccountNumber(tiers.getAccountingAccountCustomer()));
        }

        if (tiers.getAccountingAccountDeposit() != null
                && tiers.getAccountingAccountDeposit().getPrincipalAccountingAccount() != null) {
            tiersDto.setAccountingAccountDeposit(tiers.getAccountingAccountDeposit().getLabel() + " - " +
                    AccountingAccountHelper.computeAccountingAccountNumber(tiers.getAccountingAccountDeposit()));
        }

        return tiersDto;
    }

    private ResponsableDto mapResponsableToResponsableDto(Responsable responsable,
            HashMap<String, KpiCrm> kpiCrmsMap) {

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
        if (responsable.getFormalisteEmployee() != null)
            outResponsable.setFormalisteEmployee(
                    responsable.getFormalisteEmployee().getFirstname() + " "
                            + responsable.getFormalisteEmployee().getLastname());
        return outResponsable;
    }

    private ResponsableDto mapResponsableDetailsToResponsableDto(ResponsableDto responsableDto,
            Responsable responsable) {
        responsableDto.setPhones(
                responsable.getPhones() != null ? responsable.getPhones().stream().map(Phone::getPhoneNumber).toList()
                        : null);
        responsableDto.setMail(
                responsable.getMail() != null ? responsable.getMail().getMail() : null);

        return responsableDto;
    }

    private HashMap<String, KpiCrm> getKpiCrmsMap() {
        List<KpiCrm> kpiCrms = kpiCrmService.getKpiCrms();
        HashMap<String, KpiCrm> kpiCrmsMap = new HashMap<>();
        for (KpiCrm kpiCrm : kpiCrms) {
            kpiCrmsMap.put(kpiCrm.getLabel(), kpiCrm);
        }
        return kpiCrmsMap;
    }

    private String getFormatedAggregatedValue(KpiCrm kpiCrm, BigDecimal value) {
        if (kpiCrm.getUnit() != null && kpiCrm.getUnit().equals("€"))
            return String.format("%,.2f €", value);
        return value.setScale(2, RoundingMode.HALF_EVEN).toString();
    }
}
