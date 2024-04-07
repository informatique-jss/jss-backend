package com.jss.osiris.modules.quotation.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.mail.GeneratePdfDelegate;
import com.jss.osiris.modules.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.quotation.model.Domiciliation;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.repository.DomiciliationRepository;

@Service
public class DomiciliationServiceImpl implements DomiciliationService {

    @Autowired
    DomiciliationRepository domiciliationRepository;

    @Autowired
    GeneratePdfDelegate generatePdfDelegate;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    ConstantService constantService;

    @Autowired
    ProvisionService provisionService;

    @Override
    public Domiciliation getDomiciliation(Integer id) {
        Optional<Domiciliation> domiciliation = domiciliationRepository.findById(id);
        if (domiciliation.isPresent())
            return domiciliation.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Provision generateDomiciliationContract(Provision provision)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        provision = provisionService.getProvision(provision.getId());
        File publicationReceiptPdf = generatePdfDelegate.generateDomiciliationContract(provision);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmm");
        try {
            provision.setAttachments(
                    attachmentService.addAttachment(new FileInputStream(publicationReceiptPdf),
                            provision.getId(),
                            Provision.class.getSimpleName(),
                            constantService.getAttachmentTypeContract(),
                            "Domiciliation_contract_" + formatter.format(LocalDateTime.now()) + ".pdf",
                            false, "Contrat de domiciliation n°" + provision.getId(), null, null, null));
        } catch (FileNotFoundException e) {
            throw new OsirisException(e, "Impossible to read invoice PDF temp file");
        } finally {
            publicationReceiptPdf.delete();
        }
        return provision;
    }
}
