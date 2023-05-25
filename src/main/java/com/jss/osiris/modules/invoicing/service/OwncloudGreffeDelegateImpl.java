package com.jss.osiris.modules.invoicing.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.impl.SardineImpl;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.search.model.IndexEntity;
import com.jss.osiris.libs.search.service.SearchService;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.model.OwncloudGreffeFile;
import com.jss.osiris.modules.invoicing.model.OwncloudGreffeInvoice;
import com.jss.osiris.modules.invoicing.model.lutecia.FactureBean;
import com.jss.osiris.modules.invoicing.model.lutecia.LuteciaBean;
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
public class OwncloudGreffeDelegateImpl implements OwncloudGreffeDelegate {

    @Value("${owncloud.greffe.login}")
    private String login;

    @Value("${owncloud.greffe.password}")
    private String password;

    @Value("${owncloud.greffe.webdav.url}")
    private String rootUrl;

    @Value("${owncloud.greffe.webdav.folder.root}")
    private String rootFolder;

    @Autowired
    CompetentAuthorityService competentAuthorityService;

    @Autowired
    OwncloudGreffeFileService owncloudGreffeFileService;

    @Autowired
    OwncloudGreffeInvoiceService owncloudGreffeInvoiceService;

    @Autowired
    SearchService searchService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    ConstantService constantService;

    @Autowired
    DebourService debourService;

    @Autowired
    ProvisionService provisionService;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void grabAllFiles() throws OsirisException, OsirisClientMessageException, OsirisValidationException {

        Sardine sardine = new SardineImpl(login, password);
        List<DavResource> resources;
        try {
            resources = sardine.list(rootUrl + rootFolder);
        } catch (IOException e) {
            throw new OsirisException(e, "Can't load Owncloud Greffe root file list");
        }
        for (DavResource res : resources) {
            if (res.isDirectory() && !res.getPath().equals(rootFolder)) {
                CompetentAuthority competentAuthority = competentAuthorityService
                        .getCompetentAuthorityByOwncloudFolderName(res.getName());
                if (competentAuthority != null) {
                    List<DavResource> folderFiles;
                    try {
                        folderFiles = sardine.list(rootUrl + res.getPath(), 1000);
                    } catch (IOException e) {
                        throw new OsirisException(e,
                                "Can't load Owncloud Greffe file list for folder " + res.getPath());
                    }
                    for (DavResource file : folderFiles) {
                        if (!file.isDirectory() && file.getName().endsWith(".xml")) {
                            OwncloudGreffeFile owncloudGreffeFile = owncloudGreffeFileService
                                    .getOwncloudGreffeFileByCompetentAuthorityAndFilename(competentAuthority,
                                            file.getName());
                            if (owncloudGreffeFile == null) {
                                OwncloudGreffeFile newOwncloudGreffeFile = new OwncloudGreffeFile();
                                newOwncloudGreffeFile.setCompetentAuthority(competentAuthority);
                                newOwncloudGreffeFile.setFilename(file.getName());
                                owncloudGreffeFileService.addOrUpdateOwncloudGreffeFile(newOwncloudGreffeFile);
                                fetchInvoiceDetails(file, newOwncloudGreffeFile, sardine);
                            }
                        }
                    }
                }
            }
        }
    }

    private void fetchInvoiceDetails(DavResource file, OwncloudGreffeFile owncloudGreffeFile, Sardine sardine)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        String xml = "";
        try {
            xml = new String(sardine.get(rootUrl + file.getPath()).readAllBytes(), StandardCharsets.ISO_8859_1);
        } catch (IOException e) {
            throw new OsirisException(e, "Can't read Owncloud Greffe file content " + file.getPath());
        }
        XmlMapper xmlMapper = new XmlMapper();
        LuteciaBean value;
        try {
            value = xmlMapper.readValue(xml, LuteciaBean.class);
        } catch (Exception e) {
            throw new OsirisException(e, "Impossible to parse Owncloud Greffe file content " + file.getPath());
        }

        if (value != null && value.getComptabiliteBean() != null
                && value.getComptabiliteBean().getReleveFacturations() != null
                && value.getComptabiliteBean().getReleveFacturations().getFactures() != null)
            for (FactureBean facture : value.getComptabiliteBean().getReleveFacturations().getFactures()) {
                OwncloudGreffeInvoice owncloudGreffeInvoice = new OwncloudGreffeInvoice();
                owncloudGreffeInvoice
                        .setDate(LocalDate.parse(facture.getDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                owncloudGreffeInvoice
                        .setNonTaxablePrice(Float.parseFloat(facture.getNonTaxablePrice().replace(",", ".")));
                owncloudGreffeInvoice.setNumero(facture.getNumero());
                owncloudGreffeInvoice.setOwncloudGreffeFile(owncloudGreffeFile);
                owncloudGreffeInvoice.setPreTaxPrice(Float.parseFloat(facture.getPreTaxPrice().replace(",", ".")));
                owncloudGreffeInvoice.setCustomerReference(facture.getReferences());
                owncloudGreffeInvoice.setTotalPrice(Float.parseFloat(facture.getTotalPrice().replace(",", ".")));
                owncloudGreffeInvoice
                        .setTotalTaxedPrice(Float.parseFloat(facture.getTotalTaxedPrice().replace(",", ".")));
                owncloudGreffeInvoice.setVatPrice(Float.parseFloat(facture.getVatPrice().replace(",", ".")));

                if (owncloudGreffeInvoice.getDate().isAfter(LocalDate.of(2022, 12, 1)))
                    owncloudGreffeInvoice
                            .setCustomerOrder(findCustomerOrderForOwncloudGreffeInvoice(owncloudGreffeInvoice));

                if (owncloudGreffeInvoice.getCustomerOrder() != null)
                    owncloudGreffeInvoice.setDebour(findCorrespondingDebour(owncloudGreffeInvoice));

                owncloudGreffeInvoiceService.addOrUpdateOwncloudGreffeInvoice(owncloudGreffeInvoice);

                // Auto create provider invoice
                if (owncloudGreffeInvoice.getDebour() != null
                        && owncloudGreffeInvoice.getDebour().getInvoiceItem() == null) {
                    owncloudGreffeInvoice
                            .setInvoice(generateInvoiceFromDebourAndGreffeInvoice(owncloudGreffeInvoice));
                    owncloudGreffeInvoiceService.addOrUpdateOwncloudGreffeInvoice(owncloudGreffeInvoice);
                }
            }
    }

    private CustomerOrder findCustomerOrderForOwncloudGreffeInvoice(OwncloudGreffeInvoice invoice) {
        Pattern p = Pattern.compile("\\d+");

        if (invoice.getCustomerReference() != null) {
            Matcher m = p.matcher(invoice.getCustomerReference());
            while (m.find()) {
                Integer idToFind = null;
                List<IndexEntity> tmpEntitiesFound = null;
                try {
                    idToFind = Integer.parseInt(m.group());
                } catch (NumberFormatException e) {
                }

                if (idToFind != null) {
                    tmpEntitiesFound = searchService.searchForEntitiesById(idToFind,
                            Arrays.asList(CustomerOrder.class.getSimpleName()));
                }
                if (tmpEntitiesFound != null && tmpEntitiesFound.size() == 1) {
                    for (IndexEntity newEntity : tmpEntitiesFound) {
                        return customerOrderService.getCustomerOrder(newEntity.getEntityId());
                    }
                }
            }
        }
        return null;
    }

    private Debour findCorrespondingDebour(OwncloudGreffeInvoice invoice) {
        Debour debourFound = null;
        if (invoice.getCustomerOrder() != null && invoice.getCustomerOrder().getAssoAffaireOrders() != null
                && invoice.getCustomerOrder().getAssoAffaireOrders().size() > 0)
            for (AssoAffaireOrder asso : invoice.getCustomerOrder().getAssoAffaireOrders()) {
                if (asso.getProvisions() != null && asso.getProvisions().size() > 0)
                    for (Provision provision : asso.getProvisions())
                        if (provision.getDebours() != null && provision.getDebours().size() > 0)
                            for (Debour debour : provision.getDebours()) {
                                if (debour.getInvoiceItem() == null
                                        && debour.getCompetentAuthority().getId()
                                                .equals(invoice.getOwncloudGreffeFile().getCompetentAuthority().getId())
                                        && Math.round(debour.getDebourAmount() * 100f)
                                                / 100f == Math.round(invoice.getTotalPrice() * 100f) / 100f) {
                                    // Multiple matches, do nothing
                                    if (debourFound != null)
                                        return null;
                                    debourFound = debour;
                                }
                            }
            }
        return debourFound;
    }

    private Invoice generateInvoiceFromDebourAndGreffeInvoice(OwncloudGreffeInvoice greffeInvoice)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException {
        Invoice invoice = new Invoice();
        invoice.setCompetentAuthority(greffeInvoice.getOwncloudGreffeFile().getCompetentAuthority());
        invoice.setCustomerOrderForInboundInvoice(greffeInvoice.getCustomerOrder());
        invoice.setManualAccountingDocumentNumber(greffeInvoice.getNumero());
        invoice.setIsInvoiceFromProvider(true);
        invoice.setManualAccountingDocumentDate(greffeInvoice.getDate());
        for (AssoAffaireOrder asso : greffeInvoice.getCustomerOrder().getAssoAffaireOrders())
            for (Provision provision : asso.getProvisions())
                if (provision.getDebours() != null && provision.getDebours().size() > 0)
                    for (Debour debour : provision.getDebours())
                        if (debour.getId().equals(greffeInvoice.getDebour().getId())) {
                            debour.setNonTaxableAmount(greffeInvoice.getNonTaxablePrice());
                            return invoiceService.addOrUpdateInvoiceFromUser(invoice);
                        }
        return null;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Invoice generateInvoiceFromProvisionAndGreffeInvoice(OwncloudGreffeInvoice greffeInvoice,
            Provision provision) throws OsirisClientMessageException, OsirisException, OsirisValidationException {
        if (greffeInvoice.getOwncloudGreffeFile().getCompetentAuthority().getDefaultPaymentType() == null)
            throw new OsirisClientMessageException(
                    "Le mode de paiement par défaut de l'autorité compétente n'a pas été renseigné");

        if (greffeInvoice.getCustomerOrder() == null)
            greffeInvoice.setCustomerOrder(provision.getAssoAffaireOrder().getCustomerOrder());

        Debour newDebour = new Debour();
        newDebour.setBillingType(constantService.getBillingTypeEmolumentsDeGreffeDebour());
        newDebour.setComments("Créé depuis la facture " + greffeInvoice.getNumero());
        newDebour.setCompetentAuthority(greffeInvoice.getOwncloudGreffeFile().getCompetentAuthority());
        newDebour.setDebourAmount(greffeInvoice.getTotalPrice());
        newDebour.setInvoicedAmount(newDebour.getDebourAmount());
        newDebour.setPaymentDateTime(greffeInvoice.getDate().atTime(12, 0));
        newDebour.setPaymentType(newDebour.getCompetentAuthority().getDefaultPaymentType());
        newDebour.setProvision(provision);
        debourService.addOrUpdateDebour(newDebour);

        if (provision.getDebours() == null)
            provision.setDebours(new ArrayList<Debour>());
        provision.getDebours().add(newDebour);
        provisionService.addOrUpdateProvision(provision);

        greffeInvoice.setDebour(newDebour);
        greffeInvoice.setCustomerOrder(provision.getAssoAffaireOrder().getCustomerOrder());
        owncloudGreffeInvoiceService.addOrUpdateOwncloudGreffeInvoice(greffeInvoice);

        // Refresh customer order to get new debour in provision
        greffeInvoice.setCustomerOrder(customerOrderService.getCustomerOrder(greffeInvoice.getCustomerOrder().getId()));

        return generateInvoiceFromDebourAndGreffeInvoice(greffeInvoice);
    }
}
