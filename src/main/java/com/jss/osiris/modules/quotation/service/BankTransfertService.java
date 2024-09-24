package com.jss.osiris.modules.quotation.service;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.transfer.PmtInfBean;
import com.jss.osiris.modules.invoicing.model.BankTransfertSearch;
import com.jss.osiris.modules.invoicing.model.BankTransfertSearchResult;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.quotation.model.BankTransfert;

public interface BankTransfertService {
        public List<BankTransfert> getBankTransfers();

        public void reindexBankTransfert() throws OsirisException;

        public List<BankTransfertSearchResult> searchBankTransfert(BankTransfertSearch bankTransfertSearch);

        public BankTransfert getBankTransfert(Integer id);

        public BankTransfert addOrUpdateBankTransfert(BankTransfert bankTransfert) throws OsirisException;

        public BankTransfert generateBankTransfertForManualInvoice(Invoice invoice)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException;

        public File getBankTransfertExport(BankTransfertSearch transfertSearch)
                        throws OsirisException, OsirisValidationException, OsirisClientMessageException,
                        OsirisDuplicateException;

        public BankTransfert cancelBankTransfert(BankTransfert bankTransfert) throws OsirisException;

        public BankTransfert selectBankTransfertForExport(BankTransfert bankTransfert, boolean isSelected)
                        throws OsirisException;

        public PmtInfBean generateBodyForBankTransfert(String headerLabel, Float transfertAmount,
                        LocalDate executionDate,
                        String recipientLabel,
                        String iban, String bic, String transfertLabel, Boolean isOverrideExecutionDate);
}
