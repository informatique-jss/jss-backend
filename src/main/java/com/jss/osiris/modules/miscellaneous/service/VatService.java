package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.miscellaneous.model.Country;
import com.jss.osiris.modules.miscellaneous.model.Department;
import com.jss.osiris.modules.miscellaneous.model.IVat;
import com.jss.osiris.modules.miscellaneous.model.Vat;
import com.jss.osiris.modules.quotation.model.IQuotation;

public interface VatService {
        public List<Vat> getVats();

        public Vat getVat(Integer id);

        public Vat getGeographicalApplicableVatForSales(IQuotation quotation, Vat vat)
                        throws OsirisException, OsirisClientMessageException;

        public Vat getGeographicalApplicableVatForPurshase(Country country, Department departement, Vat vat)
                        throws OsirisException, OsirisClientMessageException;

        public Vat getGeographicalApplicableVatForPurshases(IVat vatTiers, Vat vat)
                        throws OsirisValidationException, OsirisException, OsirisClientMessageException;

        public Vat getGeographicalApplicableVatForSales(Invoice invoice, Vat vat)
                        throws OsirisValidationException, OsirisException, OsirisClientMessageException;

        public Vat addOrUpdateVat(Vat vat);

}
