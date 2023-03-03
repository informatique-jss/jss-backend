package com.jss.osiris.modules.quotation.service;

import java.io.File;
import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.invoicing.model.BankTransfertSearch;
import com.jss.osiris.modules.invoicing.model.BankTransfertSearchResult;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.quotation.model.BankTransfert;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Debour;

public interface BankTransfertService {
        public List<BankTransfert> getBankTransfers();

        public void reindexBankTransfert();

        public List<BankTransfertSearchResult> searchBankTransfert(BankTransfertSearch bankTransfertSearch);

        public BankTransfert getBankTransfert(Integer id);

        public BankTransfert addOrUpdateBankTransfert(BankTransfert bankTransfert);

        public BankTransfert generateBankTransfertForDebour(Debour debour, AssoAffaireOrder asso,
                        CustomerOrder customerOrder)
                        throws OsirisException, OsirisClientMessageException;

        public BankTransfert generateBankTransfertForManualInvoice(Invoice invoice)
                        throws OsirisException, OsirisClientMessageException;

        public File getBankTransfertExport(BankTransfertSearch transfertSearch) throws OsirisException;
}
