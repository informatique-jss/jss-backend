package com.jss.osiris.modules.osiris.quotation.service;

import java.io.File;
import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.invoicing.model.DirectDebitTransfertSearch;
import com.jss.osiris.modules.osiris.invoicing.model.DirectDebitTransfertSearchResult;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.quotation.model.DirectDebitTransfert;

public interface DirectDebitTransfertService {
        public List<DirectDebitTransfert> getDirectDebitTransferts();

        public DirectDebitTransfert getDirectDebitTransfert(Integer id);

        public DirectDebitTransfert addOrUpdateDirectDebitTransfert(DirectDebitTransfert directDebitTransfert)
                        throws OsirisException;

        public void reindexDirectDebitTransfert() throws OsirisException;

        public List<DirectDebitTransfertSearchResult> searchDirectDebitTransfert(
                        DirectDebitTransfertSearch directDebitTransfertSearch);

        public DirectDebitTransfert generateDirectDebitTransfertForOutboundInvoice(Invoice invoice)
                        throws OsirisException, OsirisClientMessageException;

        public File getDirectDebitTransfertExport(DirectDebitTransfertSearch transfertSearch)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public DirectDebitTransfert cancelDirectDebitTransfert(DirectDebitTransfert directDebitTransfert)
                        throws OsirisException;
}
