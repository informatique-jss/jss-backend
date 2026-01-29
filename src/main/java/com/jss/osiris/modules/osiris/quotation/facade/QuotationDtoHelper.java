package com.jss.osiris.modules.osiris.quotation.facade;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.dto.CustomerOrderDto;
import com.jss.osiris.modules.osiris.quotation.dto.ProvisionDto;
import com.jss.osiris.modules.osiris.quotation.dto.QuotationDto;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;

@org.springframework.stereotype.Service
public class QuotationDtoHelper {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    CustomerOrderService customerOrderService;

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

    public List<CustomerOrderDto> mapCustomerOrders(List<CustomerOrder> customerOrders) {
        List<CustomerOrderDto> outQuotationsDtos = new ArrayList<CustomerOrderDto>();
        if (customerOrders != null) {
            for (CustomerOrder quotation : customerOrders) {
                outQuotationsDtos.add(mapCustomerOrderToCustomerOrderDto(quotation));
            }
        }
        return outQuotationsDtos;
    }

    private CustomerOrderDto mapCustomerOrderToCustomerOrderDto(CustomerOrder customerOrder) {
        CustomerOrderDto customerOrderDto = new CustomerOrderDto();
        customerOrderDto.setId(customerOrder.getId());

        if (customerOrder.getResponsable() != null) {
            if (customerOrder.getResponsable() != null) {
                String civility = (customerOrder.getResponsable().getCivility() != null
                        ? customerOrder.getResponsable().getCivility().getLabel() + " "
                        : null);

                customerOrderDto.setResponsable(civility + customerOrder.getResponsable().getFirstname() + " "
                        + customerOrder.getResponsable().getLastname());
            }

            if (customerOrder.getResponsable().getSalesEmployee() != null)
                customerOrderDto.setSalesEmployee(
                        customerOrder.getResponsable().getSalesEmployee().getFirstname() + " "
                                + customerOrder.getResponsable().getSalesEmployee().getLastname());

            if (customerOrder.getResponsable().getTiers() != null) {
                Tiers tiers = customerOrder.getResponsable().getTiers();
                customerOrderDto.setTiers(tiers.getDenomination() != null ? tiers.getDenomination()
                        : tiers.getFirstname() + " " + tiers.getLastname());
            }

        }
        if (customerOrder.getCreatedDate() != null) {
            customerOrderDto.setCreationDate(customerOrder.getCreatedDate());
        }

        if (customerOrder.getCustomerOrderStatus() != null) {
            customerOrderDto.setStatus(customerOrder.getCustomerOrderStatus().getLabel());
        }

        if (customerOrder.getAssoAffaireOrders() != null && !customerOrder.getAssoAffaireOrders().isEmpty()) {
            List<String> affaires = new ArrayList<String>();
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders()) {
                if (asso.getAffaire() != null) {
                    affaires.add(asso.getAffaire().getDenomination());
                }
            }
            customerOrderDto.setAffaires(affaires);

            List<String> services = new ArrayList<String>();
            for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders()) {
                if (asso.getServices() != null) {
                    for (Service service : asso.getServices()) {
                        services.add(service.getServiceLabelToDisplay());
                    }
                }
            }
            customerOrderDto.setServices(services);
        }

        if (customerOrder.getCustomerOrderOrigin() != null) {
            customerOrderDto.setOrigin(customerOrder.getCustomerOrderOrigin().getLabel());
        }

        if (customerOrder.getDescription() != null) {
            customerOrderDto.setDescription(customerOrder.getDescription());
        }

        return customerOrderDto;
    }

    public List<ProvisionDto> mapProvisions(List<Provision> provisions) {
        List<ProvisionDto> outProvisionsDtos = new ArrayList<ProvisionDto>();
        if (provisions != null) {
            for (Provision provision : provisions) {
                outProvisionsDtos.add(mapProvisionToProvisionDto(provision));
            }
        }
        return outProvisionsDtos;
    }

    private ProvisionDto mapProvisionToProvisionDto(Provision provision) {
        ProvisionDto provisionDto = new ProvisionDto();
        StringBuilder builder = new StringBuilder();

        provisionDto.setId(provision.getId());

        // TODO : get productionDate
        // if (provision.getdate() != null) {
        // provisionDto.setAffaire(provision.getAffaire().getDenomination());
        // }

        if (provision.getService().getAssoAffaireOrder().getCustomerOrder() != null) {
            provisionDto.setCustomerOrderId(provision.getService().getAssoAffaireOrder().getCustomerOrder().getId());
        }

        // Responsable
        if (provision.getService().getAssoAffaireOrder().getCustomerOrder() != null
                && provision.getService().getAssoAffaireOrder().getCustomerOrder().getResponsable() != null) {
            Responsable responsable = provision.getService().getAssoAffaireOrder().getCustomerOrder().getResponsable();

            if (responsable.getCivility() != null)
                builder.append(responsable.getCivility().getLabel()).append(" ");

            builder.append(responsable.getFirstname()).append(" ").append(responsable.getLastname());
            provisionDto.setResponsable(builder.toString());

            // adding deno of tiers
            Tiers tiers = provision.getService().getAssoAffaireOrder().getCustomerOrder().getResponsable().getTiers();
            if (tiers != null && !tiers.getIsIndividual())
                provisionDto.setTiers(tiers.getDenomination());
        }

        if (provision.getService().getAssoAffaireOrder().getAffaire() != null) {
            builder = new StringBuilder();
            Affaire affaire = provision.getService().getAssoAffaireOrder().getAffaire();

            if (affaire.getIsIndividual()) {
                if (affaire.getCivility() != null)
                    builder.append(affaire.getCivility().getLabel()).append(" ").append(affaire.getFirstname())
                            .append(" ").append(affaire.getLastname());
            } else {
                builder.append(affaire.getDenomination());
            }

            if (affaire.getCity() != null)
                builder.append(" (").append(affaire.getCity().getLabel()).append(")");

            // Affaire
            provisionDto.setAffaire(builder.toString());

            // Competent authority
            if (affaire.getCompetentAuthority() != null)
                provisionDto.setCompetentAuthority(
                        provision.getService().getAssoAffaireOrder().getAffaire().getCompetentAuthority().getLabel());
        }

        if (provision.getService().getServiceLabelToDisplay() != null) {
            provisionDto.setService(provision.getService().getServiceLabelToDisplay());
        }

        if (provision.getAnnouncement() != null && provision.getAnnouncement().getConfrere() != null) {
            provisionDto.setConfrere(provision.getAnnouncement().getConfrere().getLabel());
        }

        if (provision.getAssignedTo() != null)
            provisionDto.setFormalisteEmployee(
                    provision.getAssignedTo().getFirstname() + " " + provision.getAssignedTo().getLastname());

        provisionDto.setProvisionLabel(provision.getProvisionType().getLabel());

        // TODO : update date
        // provisionDto.setUpdateDate(provision.getSimpleProvision().getSimpleProvisionStatus().getPredecessors().getLast().);

        // TODO : creationDate
        // provisionDto.setCreationdate(provision.getSimpleProvision().getSimpleProvisionStatus().getPredecessors().get(0).getdate());

        return provisionDto;
    }
}
