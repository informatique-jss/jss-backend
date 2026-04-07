package com.jss.osiris.modules.osiris.invoicing.service;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.InpiInvoicingExtractParser;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.invoicing.model.InpiInvoicingExtract;
import com.jss.osiris.modules.osiris.invoicing.repository.InpiInvoicingExtractRepository;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;

@Service
public class InpiInvoicingExtractServiceImpl implements InpiInvoicingExtractService {

    @Autowired
    InpiInvoicingExtractRepository inpiInvoicingExtractRepository;

    @Autowired
    InpiInvoicingExtractParser inpiInvoicingExtractParser;

    @Override
    public List<InpiInvoicingExtract> getInpiInvoicingExtracts() {
        return IterableUtils.toList(inpiInvoicingExtractRepository.findAll());
    }

    @Override
    public InpiInvoicingExtract getInpiInvoicingExtract(Integer id) {
        Optional<InpiInvoicingExtract> inpiInvoicingExtract = inpiInvoicingExtractRepository.findById(id);
        if (inpiInvoicingExtract.isPresent())
            return inpiInvoicingExtract.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Attachment> uploadInpiInvoicingExtractFile(InputStream file)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        InpiInvoicingExtract inpiInvoicingExtract = inpiInvoicingExtractParser.parseInpiItnvoicingExtract(file);
        return null;
    }
}
