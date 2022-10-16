package com.jss.osiris.modules.miscellaneous.service;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.AttachmentType;
import com.jss.osiris.modules.miscellaneous.repository.AttachmentRepository;
import com.jss.osiris.modules.quotation.model.Announcement;
import com.jss.osiris.modules.quotation.model.Bodacc;
import com.jss.osiris.modules.quotation.model.Domiciliation;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.service.AnnouncementService;
import com.jss.osiris.modules.quotation.service.BodaccService;
import com.jss.osiris.modules.quotation.service.DomiciliationService;
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
            AttachmentType attachmentType,
            String filename, Boolean replaceExistingAttachementType) throws IOException, NoSuchAlgorithmException {
        String absoluteFilePath = storageFileService.saveFile(file, filename,
                entityType + File.separator + idEntity);

        List<Attachment> attachments = getAttachmentForEntityType(entityType, idEntity);

        if (replaceExistingAttachementType) {
            if (entityType.equals(Tiers.class.getSimpleName())) {
                attachments = attachmentRepository.findByTiersId(idEntity);
            } else if (entityType.equals(Responsable.class.getSimpleName())) {
                attachments = attachmentRepository.findByResponsableId(idEntity);
            } else if (entityType.equals(Quotation.class.getSimpleName())) {
                attachments = attachmentRepository.findByQuotationId(idEntity);
            } else if (entityType.equals(Domiciliation.class.getSimpleName())) {
                attachments = attachmentRepository.findByDomiciliationId(idEntity);
            } else if (entityType.equals(Announcement.class.getSimpleName())) {
                attachments = attachmentRepository.findByAnnouncementId(idEntity);
            } else if (entityType.equals(Bodacc.class.getSimpleName())) {
                attachments = attachmentRepository.findByBodaccId(idEntity);
            }

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
        attachment.setAttachmentType(attachmentType);
        attachment.setIsDisabled(false);
        attachment.setUploadedFile(uploadedFileService.createUploadedFile(filename, absoluteFilePath));

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
        } else if (entityType.equals(Quotation.class.getSimpleName())) {
            Quotation quotation = quotationService.getQuotation(idEntity);
            if (quotation == null)
                return new ArrayList<Attachment>();
            attachment.setQuotation(quotation);
        } else if (entityType.equals(Domiciliation.class.getSimpleName())) {
            Domiciliation domiciliation = domiciliationService.getDomiciliation(idEntity);
            if (domiciliation == null)
                return new ArrayList<Attachment>();
            attachment.setDomiciliation(domiciliation);
        } else if (entityType.equals(Announcement.class.getSimpleName())) {
            Announcement announcement = announcementService.getAnnouncement(idEntity);
            if (announcement == null)
                return new ArrayList<Attachment>();
            attachment.setAnnouncement(announcement);
        } else if (entityType.equals(Bodacc.class.getSimpleName())) {
            Bodacc bodacc = bodaccService.getBodacc(idEntity);
            if (bodacc == null)
                return new ArrayList<Attachment>();
            attachment.setBodacc(bodacc);
        }
        addOrUpdateAttachment(attachment);

        return getAttachmentForEntityType(entityType, idEntity);
    }

    @Override
    public void deleteAttachment(Attachment attachment) {
        if (attachment != null) {
            attachmentRepository.delete(attachment);
        }
    }

    public Attachment addOrUpdateAttachment(Attachment attachment) {
        return attachmentRepository.save(attachment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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
        } else if (entityType.equals(Domiciliation.class.getSimpleName())) {
            attachments = attachmentRepository.findByDomiciliationId(idEntity);
        } else if (entityType.equals(Announcement.class.getSimpleName())) {
            attachments = attachmentRepository.findByAnnouncementId(idEntity);
        } else if (entityType.equals(Bodacc.class.getSimpleName())) {
            attachments = attachmentRepository.findByBodaccId(idEntity);
        }
        return attachments;
    }
}
