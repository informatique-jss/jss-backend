package com.jss.jssbackend.modules.miscellaneous.service;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jss.jssbackend.modules.miscellaneous.model.Attachment;
import com.jss.jssbackend.modules.miscellaneous.model.AttachmentType;
import com.jss.jssbackend.modules.miscellaneous.repository.AttachmentRepository;
import com.jss.jssbackend.modules.quotation.model.Domiciliation;
import com.jss.jssbackend.modules.quotation.model.Quotation;
import com.jss.jssbackend.modules.quotation.service.DomiciliationService;
import com.jss.jssbackend.modules.quotation.service.QuotationService;
import com.jss.jssbackend.modules.tiers.model.Responsable;
import com.jss.jssbackend.modules.tiers.model.Tiers;
import com.jss.jssbackend.modules.tiers.service.ResponsableService;
import com.jss.jssbackend.modules.tiers.service.TiersService;

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

    @Override
    public List<Attachment> getAttachments() {
        return IterableUtils.toList(attachmentRepository.findAll());
    }

    @Override
    public Attachment getAttachment(Integer id) {
        Optional<Attachment> tiersAttachment = attachmentRepository.findById(id);
        if (!tiersAttachment.isEmpty())
            return tiersAttachment.get();
        return null;
    }

    @Override
    public List<Attachment> addAttachment(MultipartFile file, Integer idEntity, String entityType,
            AttachmentType attachmentType,
            String filename, Boolean replaceExistingAttachementType) throws IOException, NoSuchAlgorithmException {
        String absoluteFilePath = storageFileService.saveFile(file, filename,
                entityType + File.separator + idEntity);

        List<Attachment> attachments = new ArrayList<Attachment>();

        if (replaceExistingAttachementType) {
            if (entityType.equals(Tiers.class.getSimpleName())) {
                attachments = attachmentRepository.findByTiersId(idEntity);
            } else if (entityType.equals(Responsable.class.getSimpleName())) {
                attachments = attachmentRepository.findByResponsableId(idEntity);
            } else if (entityType.equals(Quotation.class.getSimpleName())) {
                attachments = attachmentRepository.findByQuotationId(idEntity);
            } else if (entityType.equals(Domiciliation.class.getSimpleName())) {
                attachments = attachmentRepository.findByDomiciliationId(idEntity);
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
        attachment.setUploadedFile(uploadedFileService.createUploadedFile(filename, absoluteFilePath));

        if (entityType.equals(Tiers.class.getSimpleName())) {
            Tiers tiers = tiersService.getTiersById(idEntity);
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
        }
        attachmentRepository.save(attachment);

        if (entityType.equals(Tiers.class.getSimpleName())) {
            attachments = attachmentRepository.findByTiersId(idEntity);
        } else if (entityType.equals(Responsable.class.getSimpleName())) {
            attachments = attachmentRepository.findByResponsableId(idEntity);
        } else if (entityType.equals(Quotation.class.getSimpleName())) {
            attachments = attachmentRepository.findByQuotationId(idEntity);
        } else if (entityType.equals(Domiciliation.class.getSimpleName())) {
            attachments = attachmentRepository.findByDomiciliationId(idEntity);
        }

        return attachments;
    }

    @Override
    public void deleteAttachment(Attachment attachment) {
        if (attachment != null) {
            attachmentRepository.delete(attachment);
        }
    }
}
