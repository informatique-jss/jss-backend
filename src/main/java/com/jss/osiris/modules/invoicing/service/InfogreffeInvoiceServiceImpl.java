package com.jss.osiris.modules.invoicing.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.invoicing.model.InfogreffeInvoice;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.repository.InfogreffeInvoiceRepository;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.service.CompetentAuthorityService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Debour;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.quotation.service.DebourService;
import com.jss.osiris.modules.quotation.service.ProvisionService;

@Service
public class InfogreffeInvoiceServiceImpl implements InfogreffeInvoiceService {

    @Autowired
    InfogreffeInvoiceRepository infogreffeInvoiceRepository;

    @Autowired
    CompetentAuthorityService competentAuthorityService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    ConstantService constantService;

    @Autowired
    DebourService debourService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    ProvisionService provisionService;

    @Override
    public List<InfogreffeInvoice> getInfogreffeInvoices() {
        return infogreffeInvoiceRepository.findByDebourIsNull();
    }

    @Override
    public List<InfogreffeInvoice> getInfogreffeInvoicesByCustomerReference(String customerReference) {
        return infogreffeInvoiceRepository
                .findByCustomerReferenceContainingIgnoreCaseAndDebourIsNull(customerReference);
    }

    @Override
    public InfogreffeInvoice getInfogreffeInvoice(Integer id) {
        Optional<InfogreffeInvoice> infogreffeInvoice = infogreffeInvoiceRepository.findById(id);
        if (infogreffeInvoice.isPresent())
            return infogreffeInvoice.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InfogreffeInvoice addOrUpdateInfogreffeInvoice(
            InfogreffeInvoice infogreffeInvoice) {
        return infogreffeInvoiceRepository.save(infogreffeInvoice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean importInfogreffeInvoices(String csv)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        if (csv != null && csv.length() > 0 && csv.contains(";")) {
            String[] lines = csv.split("\\r?\\n|\\r");
            for (String line : lines) {
                String[] fields = line.split(";");
                InfogreffeInvoice invoice = new InfogreffeInvoice();
                List<CompetentAuthority> competentAuthorities = competentAuthorityService
                        .getCompetentAuthorityByInpiReference("G" + fields[2]);
                invoice.setCompetentAuthority(competentAuthorities.get(0));
                if (fields.length > 10)
                    invoice.setCustomerReference(fields[10]);
                if (!fields[5].equals(""))
                    invoice.setInvoiceDateTime(
                            LocalDateTime.parse(fields[5], DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                invoice.setInvoiceNumber(fields[1]);
                invoice.setPreTaxPrice(Float.parseFloat(fields[9].replace("€", "").replace(",", ".")));
                Float totalPrice = Float.parseFloat(fields[8].replace("€", "").replace(",", "."));
                invoice.setVatPrice(totalPrice - invoice.getPreTaxPrice());
                invoice.setProductLabel(fields[4]);
                invoice.setSirenAffaire(fields[11]);

                InfogreffeInvoice foundInvoice = infogreffeInvoiceRepository
                        .findByInvoiceDateTimeAndSirenAffaireAndCustomerReference(invoice.getInvoiceDateTime(),
                                invoice.getSirenAffaire(), invoice.getCustomerReference());
                if (foundInvoice == null) {
                    foundInvoice = addOrUpdateInfogreffeInvoice(invoice);
                }
                foundInvoice.setDebour(findCorrespondingDebour(invoice));
                addOrUpdateInfogreffeInvoice(foundInvoice);
                System.out.println(line);
                if (foundInvoice.getDebour() != null && foundInvoice.getDebour().getInvoiceItem() == null)
                    generateInvoiceFromDebourAndInfogreffeInvoice(foundInvoice);
            }
        }
        return true;
    }

    private Debour findCorrespondingDebour(InfogreffeInvoice invoice) throws OsirisException {
        Debour debourFound = null;

        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(invoice.getCustomerReference());
        if (m.find()) {
            CustomerOrder customerOrder = customerOrderService.getCustomerOrder(Integer.parseInt(m.group()));
            if (customerOrder != null && customerOrder.getAssoAffaireOrders() != null
                    && customerOrder.getAssoAffaireOrders().size() > 0) {
                for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders()) {
                    if (asso.getProvisions() != null && asso.getProvisions().size() > 0) {
                        for (Provision provision : asso.getProvisions()) {
                            if (provision.getDebours() != null && provision.getDebours().size() > 0) {
                                for (Debour debour : provision.getDebours()) {
                                    if (debour.getInvoiceItem() == null
                                            && debour.getCompetentAuthority().getId()
                                                    .equals(constantService.getCompetentAuthorityInfogreffe().getId())
                                            && Math.round(debour.getDebourAmount() * 100f)
                                                    / 100f == Math.round(
                                                            (invoice.getPreTaxPrice() + invoice.getVatPrice()) * 100f)
                                                            / 100f) {
                                        // Multiple matches, do nothing
                                        if (debourFound != null)
                                            return null;
                                        debourFound = debour;
                                    }
                                }
                            }
                        }
                    }
                }

                if (debourFound == null) {
                    Provision provisionFound = null;
                    int nbrProvisionFound = 0;
                    // If only one provision non AL, autocreate debour
                    for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders()) {
                        if (asso.getProvisions() != null && asso.getProvisions().size() > 0) {
                            for (Provision provision : asso.getProvisions()) {
                                if (provision.getAnnouncement() == null) {
                                    provisionFound = provision;
                                    nbrProvisionFound++;
                                }
                            }
                        }
                    }
                    if (nbrProvisionFound == 1) {
                        return generateDebourFromInfogreffeInvoice(invoice, provisionFound);
                    }
                }
            }
        }

        return debourFound;
    }

    private Debour generateDebourFromInfogreffeInvoice(InfogreffeInvoice invoice, Provision provision)
            throws OsirisException {
        Debour newDebour = new Debour();
        newDebour.setBillingType(constantService.getBillingTypeInfogreffeDebour());
        newDebour.setComments("Créé depuis la facture Infogreffe " + invoice.getInvoiceNumber() + " / "
                + invoice.getCompetentAuthority().getLabel());
        newDebour.setCompetentAuthority(constantService.getCompetentAuthorityInfogreffe());
        newDebour.setDebourAmount(invoice.getPreTaxPrice() + invoice.getVatPrice());
        newDebour.setInvoicedAmount(newDebour.getDebourAmount());
        newDebour.setIsAssociated(false);
        newDebour.setPaymentDateTime(invoice.getInvoiceDateTime());
        newDebour.setPaymentType(constantService.getPaymentTypePrelevement());
        newDebour.setProvision(provision);
        return debourService.addOrUpdateDebour(newDebour);
    }

    private Invoice generateInvoiceFromDebourAndInfogreffeInvoice(InfogreffeInvoice greffeInvoice)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        Invoice invoice = new Invoice();
        invoice.setCompetentAuthority(constantService.getCompetentAuthorityInfogreffe());
        invoice.setCustomerOrderForInboundInvoice(customerOrderService.getCustomerOrder(
                greffeInvoice.getDebour().getProvision().getAssoAffaireOrder().getCustomerOrder().getId()));
        invoice.setManualAccountingDocumentNumber(greffeInvoice.getInvoiceNumber());
        invoice.setIsInvoiceFromProvider(true);
        invoice.setManualAccountingDocumentDate(greffeInvoice.getInvoiceDateTime().toLocalDate());
        for (AssoAffaireOrder asso : invoice.getCustomerOrderForInboundInvoice().getAssoAffaireOrders())
            for (Provision provision : asso.getProvisions())
                if (provision.getDebours() != null && provision.getDebours().size() > 0)
                    for (Debour debour : provision.getDebours())
                        if (debour.getId().equals(greffeInvoice.getDebour().getId())) {
                            debour.setNonTaxableAmount(0f);
                            return invoiceService.addOrUpdateInvoiceFromUser(invoice);
                        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Invoice generateInvoiceFromProvisionAndGreffeInvoice(InfogreffeInvoice greffeInvoice, Provision provision)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        greffeInvoice = getInfogreffeInvoice(greffeInvoice.getId());
        greffeInvoice.setDebour(generateDebourFromInfogreffeInvoice(greffeInvoice, provision));
        addOrUpdateInfogreffeInvoice(greffeInvoice);
        if (provision.getDebours() == null)
            provision.setDebours(new ArrayList<Debour>());
        provision.getDebours().add(greffeInvoice.getDebour());
        provisionService.addOrUpdateProvision(provision);
        return generateInvoiceFromDebourAndInfogreffeInvoice(greffeInvoice);
    }

}