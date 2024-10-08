package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.osiris.miscellaneous.model.Vat;
import com.jss.osiris.modules.osiris.quotation.model.IQuotation;

public interface VatService {
        public List<Vat> getVats();

        public Vat getVat(Integer id);

        public Vat addOrUpdateVat(Vat vat);

        public void completeVatOnInvoiceItem(InvoiceItem invoiceItem, Invoice invoice)
                        throws OsirisValidationException, OsirisException, OsirisClientMessageException;

        public void completeVatOnInvoiceItem(InvoiceItem invoiceItem, IQuotation customerOrder)
                        throws OsirisException, OsirisClientMessageException;

}
