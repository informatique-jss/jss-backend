package com.jss.osiris.modules.osiris.invoicing.service;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.invoicing.model.InpiInvoicingExtract;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;

public interface InpiInvoicingExtractService {

    public List<Attachment> uploadInpiInvoicingExtractFile(InputStream file) throws OsirisException;

    public List<InpiInvoicingExtract> getInpiInvoicingExtractByDateBetween(LocalDate startDate, LocalDate endDate);
}
