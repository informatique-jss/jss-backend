package com.jss.osiris.modules.miscellaneous.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.PdfTools;
import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.mail.CustomerMailService;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.libs.mail.model.CustomerMail;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.invoicing.service.PaymentService;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.AttachmentType;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.model.Provider;
import com.jss.osiris.modules.miscellaneous.repository.AttachmentRepository;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.model.guichetUnique.PiecesJointe;
import com.jss.osiris.modules.quotation.service.AffaireService;
import com.jss.osiris.modules.quotation.service.AnnouncementService;
import com.jss.osiris.modules.quotation.service.BodaccService;
import com.jss.osiris.modules.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.quotation.service.CustomerOrderStatusService;
import com.jss.osiris.modules.quotation.service.DomiciliationService;
import com.jss.osiris.modules.quotation.service.FormaliteService;
import com.jss.osiris.modules.quotation.service.ProvisionService;
import com.jss.osiris.modules.quotation.service.QuotationService;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;
import com.jss.osiris.modules.tiers.service.ResponsableService;
import com.jss.osiris.modules.tiers.service.TiersService;

@Service
public class AttachmentServiceImpl implements AttachmentService {

    @Autowired
    AttachmentRepository attachmentRepository;

    @Autowired
    UploadedFileService uploadedFileService;

    @Autowired
    StorageFileService storageFileService;

    @Autowired
    TiersService tiersService;

    @Autowired
    ResponsableService responsableService;

    @Autowired
    QuotationService quotationService;

    @Autowired
    DomiciliationService domiciliationService;

    @Autowired
    AnnouncementService announcementService;

    @Autowired
    BodaccService bodaccService;

    @Autowired
    FormaliteService formaliteService;

    @Autowired
    ProvisionService provisionService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    PaymentService paymentService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    AffaireService affaireService;

    @Autowired
    ActiveDirectoryHelper activeDirectoryHelper;

    @Autowired
    CustomerMailService customerMailService;

    @Autowired
    CompetentAuthorityService competentAuthorityService;

    @Autowired
    ProviderService providerService;

    @Autowired
    ConstantService constantService;

    @Autowired
    MailHelper mailHelper;

    @Autowired
    NotificationService notificationService;

    @Autowired
    CustomerOrderStatusService customerOrderStatusService;

    @Autowired
    BatchService batchService;

    @Autowired
    PdfTools pdfTools;

    @Override
    public List<Attachment> getAttachments() {
        return IterableUtils.toList(attachmentRepository.findAll());
    }

    @Override
    public Attachment getAttachment(Integer id) {
        Optional<Attachment> tiersAttachment = attachmentRepository.findById(id);
        if (tiersAttachment.isPresent())
            return tiersAttachment.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Attachment> addAttachment(MultipartFile file, Integer idEntity, String entityType,
            AttachmentType attachmentType, String filename, Boolean replaceExistingAttachementType, Integer fromPage,
            Integer toPage)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {
        try {
            return addAttachment(file.getInputStream(), idEntity, entityType, attachmentType, filename,
                    replaceExistingAttachementType, filename, null, fromPage, toPage);
        } catch (IOException e) {
            throw new OsirisException(e, "Error when reading file");
        }
    }

    @Override
    public List<Attachment> addAttachment(InputStream file, Integer idEntity, String entityType,
            AttachmentType attachmentType, String filename, Boolean replaceExistingAttachementType, String description,
            PiecesJointe piecesJointe, Integer fromPage, Integer toPage)
            throws OsirisException, OsirisClientMessageException, OsirisValidationException, OsirisDuplicateException {

        if (entityType.equals("Ofx"))
            if (activeDirectoryHelper.isUserHasGroup(ActiveDirectoryHelper.ACCOUNTING_GROUP)
                    || activeDirectoryHelper.isUserHasGroup(ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE_GROUP))
                return this.paymentService.uploadOfxFile(file);
            else
                return null;

        if (filename.toLowerCase().endsWith(".pdf")) {
            if (fromPage != null && toPage != null)
                file = pdfTools.keepPages(file, fromPage, toPage);
            file = pdfTools.optimizePdf(file);
        }

        String absoluteFilePath = storageFileService.saveFile(file, filename,
                entityType + File.separator + idEntity);

        List<Attachment> attachments = getAttachmentForEntityType(entityType, idEntity);

        if (replaceExistingAttachementType) {
            attachments = getAttachmentForEntityType(entityType, idEntity);

            if (attachments != null && attachments.size() > 0) {
                for (Attachment attachment : attachments) {
                    if (attachment.getAttachmentType().getCode().equals(attachmentType.getCode())) {
                        storageFileService.deleteFile(attachment.getUploadedFile().getPath());
                        uploadedFileService.deleteUploadedFile(attachment.getUploadedFile());
                        deleteAttachment(attachment);
                    }
                }
            }
        }

        Attachment attachment = new Attachment();
        attachment.setCreatDateTime(LocalDateTime.now());
        attachment.setAttachmentType(attachmentType);
        attachment.setIsDisabled(false);
        attachment.setDescription(description);
        attachment.setUploadedFile(uploadedFileService.createUploadedFile(filename, absoluteFilePath));
        if (piecesJointe != null)
            attachment.setPiecesJointe(piecesJointe);

        if (entityType.equals(Tiers.class.getSimpleName())) {
            Tiers tiers = tiersService.getTiers(idEntity);
            if (tiers == null)
                return new ArrayList<Attachment>();
            attachment.setTiers(tiers);
        } else if (entityType.equals(Responsable.class.getSimpleName())) {
            Responsable responsable = responsableService.getResponsable(idEntity);
            if (responsable == null)
                return new ArrayList<Attachment>();
            attachment.setResponsable(responsable);
        } else if (entityType.equals(Provider.class.getSimpleName())) {
            Provider provider = providerService.getProvider(idEntity);
            if (provider == null)
                return new ArrayList<Attachment>();
            attachment.setProvider(provider);
        } else if (entityType.equals(CompetentAuthority.class.getSimpleName())) {
            CompetentAuthority competentAuthority = competentAuthorityService.getCompetentAuthority(idEntity);
            if (competentAuthority == null)
                return new ArrayList<Attachment>();
            attachment.setCompetentAuthority(competentAuthority);
        } else if (entityType.equals(Quotation.class.getSimpleName())) {
            Quotation quotation = quotationService.getQuotation(idEntity);
            if (quotation == null)
                return new ArrayList<Attachment>();
            attachment.setQuotation(quotation);
        } else if (entityType.equals(Provision.class.getSimpleName())) {
            Provision provision = provisionService.getProvision(idEntity);
            if (provision == null)
                return new ArrayList<Attachment>();
            attachment.setProvision(provision);

            // Send immediatly to customer order
            if (attachment.getAttachmentType().getIsToSentOnUpload()) {
                addOrUpdateAttachment(attachment);
                mailHelper.sendCustomerOrderAttachmentsToCustomer(provision.getAssoAffaireOrder().getCustomerOrder(),
                        provision.getAssoAffaireOrder(), false, Arrays.asList(attachment));
            }

            // Notify user
            notificationService.notifyAttachmentAddToProvision(provision, attachment);
        } else if (entityType.equals(CustomerOrder.class.getSimpleName())) {
            CustomerOrder customerOrder = customerOrderService.getCustomerOrder(idEntity);
            if (customerOrder == null)
                return new ArrayList<Attachment>();
            attachment.setCustomerOrder(customerOrder);
        } else if (entityType.equals(Invoice.class.getSimpleName())) {
            Invoice invoice = invoiceService.getInvoice(idEntity);
            if (invoice == null)
                return new ArrayList<Attachment>();
            attachment.setInvoice(invoice);
        } else if (entityType.equals(Affaire.class.getSimpleName())) {
            Affaire affaire = affaireService.getAffaire(idEntity);
            if (affaire == null)
                return new ArrayList<Attachment>();
            attachment.setAffaire(affaire);
        } else if (entityType.equals(CustomerMail.class.getSimpleName())) {
            CustomerMail mail = customerMailService.getMail(idEntity);
            if (mail == null)
                return new ArrayList<Attachment>();
            attachment.setCustomerMail(mail);
        }
        addOrUpdateAttachment(attachment);
        attachment = getAttachment(attachment.getId());

        // Batchs
        if (entityType.equals(CompetentAuthority.class.getSimpleName()) && attachment.getAttachmentType().getId()
                .equals(constantService.getAttachmentTypeBillingClosure().getId())) {
            batchService.declareNewBatch(Batch.DO_OCR_ON_RECEIPT, attachment.getId());
        } else if (entityType.equals(Provision.class.getSimpleName())
                && (attachment.getDescription() == null || attachment.getDescription().toLowerCase().endsWith(".pdf"))
                && attachment.getAttachmentType().getId()
                        .equals(constantService.getAttachmentTypeProviderInvoice().getId())) {
            if (attachment.getProvision() != null && attachment.getProvision().getAssoAffaireOrder() != null
                    && attachment.getProvision().getAssoAffaireOrder().getCustomerOrder() != null) {
                CustomerOrder customerOrder = attachment.getProvision().getAssoAffaireOrder().getCustomerOrder();
                if (!customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.ABANDONED)
                        && !customerOrder.getCustomerOrderStatus().getCode().equals(CustomerOrderStatus.BILLED))
                    batchService.declareNewBatch(Batch.DO_OCR_ON_INVOICE, attachment.getId());
            }
        }

        return getAttachmentForEntityType(entityType, idEntity);
    }

    @Override
    public void deleteAttachment(Attachment attachment) {
        if (attachment != null) {
            attachmentRepository.delete(attachment);
        }
    }

    @Override
    public Attachment addOrUpdateAttachment(Attachment attachment) {
        if (attachment != null && attachment.getIsAlreadySent() == null)
            attachment.setIsAlreadySent(false);
        return attachmentRepository.save(attachment);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void disableDocument(Attachment attachment) {
        attachment.setIsDisabled(true);
        addOrUpdateAttachment(attachment);
    }

    private List<Attachment> getAttachmentForEntityType(String entityType, Integer idEntity) {
        List<Attachment> attachments = new ArrayList<Attachment>();
        if (entityType.equals(Tiers.class.getSimpleName())) {
            attachments = attachmentRepository.findByTiersId(idEntity);
        } else if (entityType.equals(Responsable.class.getSimpleName())) {
            attachments = attachmentRepository.findByResponsableId(idEntity);
        } else if (entityType.equals(Quotation.class.getSimpleName())) {
            attachments = attachmentRepository.findByQuotationId(idEntity);
        } else if (entityType.equals(Provision.class.getSimpleName())) {
            attachments = attachmentRepository.findByProvisionId(idEntity);
        } else if (entityType.equals(CustomerOrder.class.getSimpleName())) {
            attachments = attachmentRepository.findByCustomerOrderId(idEntity);
        } else if (entityType.equals(Invoice.class.getSimpleName())) {
            attachments = attachmentRepository.findByInvoiceId(idEntity);
        } else if (entityType.equals(Affaire.class.getSimpleName())) {
            attachments = attachmentRepository.findByAffaireId(idEntity);
        } else if (entityType.equals(CustomerMail.class.getSimpleName())) {
            attachments = attachmentRepository.findByCustomerMailId(idEntity);
        } else if (entityType.equals(Provider.class.getSimpleName())) {
            attachments = attachmentRepository.findByProviderId(idEntity);
        } else if (entityType.equals(CompetentAuthority.class.getSimpleName())) {
            attachments = attachmentRepository.findByCompetentAuthorityId(idEntity);
        }
        return attachments;
    }

    @Override
    public List<Attachment> sortAttachmentByDateDesc(List<Attachment> attachments) {
        if (attachments != null)
            attachments.sort(new Comparator<Attachment>() {
                @Override
                public int compare(Attachment o1, Attachment o2) {
                    if (o2.getCreatDateTime() == null && o1.getCreatDateTime() != null)
                        return -1;
                    if (o2.getCreatDateTime() != null && o1.getCreatDateTime() == null)
                        return 1;
                    if (o1.getCreatDateTime() == null && o2.getCreatDateTime() == null)
                        return 0;
                    return o2.getCreatDateTime().isAfter(o1.getCreatDateTime()) ? 1 : -1;
                }
            });
        return attachments;
    }

    @Override
    public Attachment cloneAttachment(Attachment attachment) throws OsirisException {
        if (attachment == null)
            throw new OsirisException(null, "Empty attachment to clone");

        Attachment newAttachment = new Attachment();
        newAttachment.setAttachmentType(attachment.getAttachmentType());
        newAttachment.setCompetentAuthority(attachment.getCompetentAuthority());
        newAttachment.setCreatDateTime(LocalDateTime.now());
        newAttachment.setCustomerMail(attachment.getCustomerMail());
        newAttachment.setCustomerOrder(attachment.getCustomerOrder());
        newAttachment.setDescription(attachment.getDescription());
        newAttachment.setInvoice(attachment.getInvoice());
        newAttachment.setAffaire(attachment.getAffaire());
        newAttachment.setIsDisabled(attachment.getIsDisabled());
        newAttachment.setProvider(attachment.getProvider());
        newAttachment.setProvision(attachment.getProvision());
        newAttachment.setQuotation(attachment.getQuotation());
        newAttachment.setResponsable(attachment.getResponsable());
        newAttachment.setTiers(attachment.getTiers());
        newAttachment.setUploadedFile(attachment.getUploadedFile());
        return newAttachment;
    }
}
