package com.jss.osiris.modules.quotation.service;

import java.io.File;
import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.invoicing.model.DirectDebitTransfertSearch;
import com.jss.osiris.modules.invoicing.model.DirectDebitTransfertSearchResult;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.quotation.model.DirectDebitTransfert;

public interface DirectDebitTransfertService {
        public List<DirectDebitTransfert> getDirectDebitTransferts();

        public DirectDebitTransfert getDirectDebitTransfert(Integer id);

        public DirectDebitTransfert addOrUpdateDirectDebitTransfert(DirectDebitTransfert directDebitTransfert);

        public void reindexDirectDebitTransfert();

        public List<DirectDebitTransfertSearchResult> searchDirectDebitTransfert(
                        DirectDebitTransfertSearch directDebitTransfertSearch);

        public DirectDebitTransfert generateDirectDebitTransfertForOutboundInvoice(Invoice invoice)
                        throws OsirisException, OsirisClientMessageException;

        public File getDirectDebitTransfertExport(DirectDebitTransfertSearch transfertSearch) throws OsirisException;
}
