package com.jss.osiris.modules.osiris.invoicing.service;

import java.io.InputStream;
import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.invoicing.model.InpiInvoicingExtract;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;

public interface InpiInvoicingExtractService {
    public List<InpiInvoicingExtract> getInpiInvoicingExtracts();

    public InpiInvoicingExtract getInpiInvoicingExtract(Integer id);

    public List<Attachment> uploadInpiInvoicingExtractFile(InputStream file)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException,
            OsirisDuplicateException;
}
