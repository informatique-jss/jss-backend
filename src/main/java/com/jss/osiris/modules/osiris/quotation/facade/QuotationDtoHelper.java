package com.jss.osiris.modules.osiris.quotation.facade;

import java.util.ArrayList;
import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.quotation.dto.QuotationDto;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;

@org.springframework.stereotype.Service
public class QuotationDtoHelper {

    // @Autowired
    // private CustomerOrderService customerOrderService;

    public List<QuotationDto> mapQuotations(List<Quotation> quotations) throws OsirisException {
        List<QuotationDto> outQuotationsDtos = new ArrayList<QuotationDto>();
        if (quotations != null) {
            for (Quotation quotation : quotations) {
                outQuotationsDtos.add(mapQuotationToQuotationDto(quotation));
            }
        }
        return outQuotationsDtos;
    }

    public QuotationDto mapQuotation(Quotation quotation) throws OsirisException {
        QuotationDto outQuotationDto = new QuotationDto();
        if (quotation != null) {
            outQuotationDto = mapQuotationToQuotationDto(quotation);
            mapQuotationDetailsToQuotationDto(outQuotationDto, quotation);
        }
        return outQuotationDto;
    }

    private QuotationDto mapQuotationToQuotationDto(Quotation quotation) throws OsirisException {
        QuotationDto quotationDto = new QuotationDto();
        quotationDto.setId(quotation.getId());

        if (quotation.getResponsable() != null) {
            if (quotation.getResponsable() != null) {
                String civility = (quotation.getResponsable().getCivility() != null
                        ? quotation.getResponsable().getCivility().getLabel() + " "
                        : null);

                quotationDto.setResponsable(civility + quotation.getResponsable().getFirstname() + " "
                        + quotation.getResponsable().getLastname());
            }

            if (quotation.getResponsable().getSalesEmployee() != null)
                quotationDto.setSalesEmployee(
                        quotation.getResponsable().getSalesEmployee().getFirstname() + " "
                                + quotation.getResponsable().getSalesEmployee().getLastname());

            if (quotation.getResponsable().getTiers() != null) {
                Tiers tiers = quotation.getResponsable().getTiers();
                quotationDto.setTiers(tiers.getDenomination() != null ? tiers.getDenomination()
                        : tiers.getFirstname() + " " + tiers.getLastname());
            }

        }
        if (quotation.getCreatedDate() != null) {
            quotationDto.setCreationDate(quotation.getCreatedDate());
        }

        if (quotation.getQuotationStatus() != null) {
            quotationDto.setStatus(quotation.getQuotationStatus().getLabel());
        }

        if (quotation.getAssoAffaireOrders() != null && !quotation.getAssoAffaireOrders().isEmpty()) {
            List<String> affaires = new ArrayList<String>();
            for (AssoAffaireOrder asso : quotation.getAssoAffaireOrders()) {
                if (asso.getAffaire() != null) {
                    affaires.add(asso.getAffaire().getDenomination());
                }
            }
            quotationDto.setAffaires(affaires);

            List<String> services = new ArrayList<String>();
            for (AssoAffaireOrder asso : quotation.getAssoAffaireOrders()) {
                if (asso.getServices() != null) {
                    for (Service service : asso.getServices()) {
                        services.add(service.getServiceLabelToDisplay());
                    }
                }
            }
            quotationDto.setServices(services);
        }

        if (quotation.getCustomerOrderOrigin() != null) {
            quotationDto.setOrigin(quotation.getCustomerOrderOrigin().getLabel());
        }

        if (quotation.getDescription() != null) {
            quotationDto.setDescription(quotation.getDescription());
        }

        // quotationDto.setTotalPrice(customerOrderService.getInvoicingSummaryForIQuotation(quotation).getTotalPrice());

        return quotationDto;
    }

    // Details of the quotation
    private QuotationDto mapQuotationDetailsToQuotationDto(QuotationDto quotationDto, Quotation quotation) {

        return quotationDto;
    }
}
