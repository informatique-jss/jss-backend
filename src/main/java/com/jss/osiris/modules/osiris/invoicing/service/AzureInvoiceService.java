package com.jss.osiris.modules.osiris.invoicing.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.invoicing.model.AzureInvoice;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.osiris.quotation.model.Provision;

public interface AzureInvoiceService {
        public AzureInvoice getAzureInvoice(Integer id);

        public AzureInvoice addOrUpdateAzureInvoice(AzureInvoice azureInvoice) throws OsirisException;

        public List<AzureInvoice> searchAzureInvoicesByInvoiceId(String invoiceId);

        public void analyseInvoice(Attachment attachment)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public AzureInvoice getAzureInvoiceByInvoiceId(String invoiceId);

        public Invoice generateInvoiceFromAzureInvoice(AzureInvoice azureInvoice,
                        Provision currentProvision)
                        throws OsirisClientMessageException, OsirisException, OsirisValidationException;

        public List<AzureInvoice> findByCompetentAuthorityAndInvoiceId(CompetentAuthority competentAuthority,
                        String invoiceId);

        public List<AzureInvoice> findByCompetentAuthorityAndInvoiceIdContains(CompetentAuthority competentAuthority,
                        String invoiceId);
}
