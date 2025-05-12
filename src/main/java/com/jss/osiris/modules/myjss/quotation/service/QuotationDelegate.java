package com.jss.osiris.modules.myjss.quotation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.TiersValidationHelper;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.service.DocumentService;
import com.jss.osiris.modules.osiris.quotation.controller.QuotationValidationHelper;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.IQuotation;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.quotation.service.QuotationService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;
import com.jss.osiris.modules.osiris.tiers.service.TiersService;

public class QuotationDelegate {

    @Autowired
    ResponsableService responsableService;

    @Autowired
    TiersValidationHelper tiersValidationHelper;

    @Autowired
    TiersService tiersService;

    @Autowired
    DocumentService documentService;

    @Autowired
    QuotationValidationHelper quotationValidationHelper;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    QuotationService quotationService;

    @Transactional(rollbackFor = Exception.class)
    public IQuotation validateAndCreateQuotation(IQuotation quotation) throws OsirisException {

        Responsable responsable = null;
        if (quotation.getResponsable() != null && quotation.getResponsable().getMail() != null) {
            responsable = responsableService.getResponsableByMail(quotation.getResponsable().getMail().getMail());
            if (responsable != null)
                quotation.setResponsable(responsable);

            else {
                Boolean isTiersValid = tiersValidationHelper.validateTiers(quotation.getResponsable().getTiers());

                if (isTiersValid) {
                    // Cloning quotation documents on Responsable and Tiers
                    for (Document quotationDoc : quotation.getDocuments()) {
                        for (Document respDoc : quotation.getResponsable().getDocuments()) {
                            if (respDoc.getDocumentType() == quotationDoc.getDocumentType())
                                documentService.cloneOrMergeDocument(quotationDoc, respDoc);
                        }
                        for (Document tiersDoc : quotation.getResponsable().getTiers().getDocuments()) {
                            if (tiersDoc.getDocumentType() == quotationDoc.getDocumentType())
                                documentService.cloneOrMergeDocument(quotationDoc, tiersDoc);
                        }
                    }
                    tiersService.addOrUpdateTiers(quotation.getResponsable().getTiers());
                }
            }
        }

        // Validate Quotation or CustomerOrder
        quotationValidationHelper.validateQuotationAndCustomerOrder(quotation, null);

        // Save Quotation or CustomerOrder
        if (!quotation.getIsQuotation())
            quotation = customerOrderService.addOrUpdateCustomerOrder((CustomerOrder) quotation, false, false);
        if (quotation.getIsQuotation())
            quotation = quotationService.addOrUpdateQuotation((Quotation) quotation);

        return quotation;
    }
}
