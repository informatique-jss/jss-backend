package com.jss.jssbackend.modules.tiers.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.miscellaneous.service.StorageFileService;
import com.jss.jssbackend.modules.miscellaneous.service.UploadedFileService;
import com.jss.jssbackend.modules.tiers.model.AttachmentType;
import com.jss.jssbackend.modules.tiers.model.Responsable;
import com.jss.jssbackend.modules.tiers.model.Tiers;
import com.jss.jssbackend.modules.tiers.model.TiersAttachment;
import com.jss.jssbackend.modules.tiers.repository.TiersAttachmentRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TiersAttachmentServiceImpl implements TiersAttachmentService {

    @Autowired
    TiersAttachmentRepository tiersAttachmentRepository;

    @Autowired
    UploadedFileService uploadedFileService;

    @Autowired
    StorageFileService storageFileService;

    @Autowired
    TiersService tiersService;

    @Override
    public List<TiersAttachment> getTiersAttachments() {
        return IterableUtils.toList(tiersAttachmentRepository.findAll());
    }

    @Override
    public TiersAttachment getTiersAttachment(Integer id) {
        Optional<TiersAttachment> tiersAttachment = tiersAttachmentRepository.findById(id);
        if (!tiersAttachment.isEmpty())
            return tiersAttachment.get();
        return null;
    }

    @Override
    public List<TiersAttachment> addTiersAttachment(MultipartFile file, Tiers tiers, AttachmentType attachmentType,
            String filename) throws IOException, NoSuchAlgorithmException {
        String absoluteFilePath = storageFileService.saveFile(file, filename);

        TiersAttachment tiersAttachment = new TiersAttachment();
        tiersAttachment.setAttachmentType(attachmentType);
        tiersAttachment.setUploadedFile(uploadedFileService.createUploadedFile(filename, absoluteFilePath));
        tiersAttachment.setTiers(tiers);
        tiersAttachmentRepository.save(tiersAttachment);

        return tiersAttachmentRepository.findByTiers(tiers);
    }

    @Override
    public List<TiersAttachment> addResponsableAttachment(MultipartFile file, Responsable responsable,
            AttachmentType attachmentType,
            String filename) throws IOException, NoSuchAlgorithmException {
        String absoluteFilePath = storageFileService.saveFile(file, filename);

        TiersAttachment tiersAttachment = new TiersAttachment();
        tiersAttachment.setAttachmentType(attachmentType);
        tiersAttachment.setUploadedFile(uploadedFileService.createUploadedFile(filename, absoluteFilePath));
        tiersAttachment.setResponsable(responsable);
        tiersAttachmentRepository.save(tiersAttachment);

        return tiersAttachmentRepository.findByResponsable(responsable);
    }

    @Override
    public List<TiersAttachment> getAttachementsByFilenameAndTiers(String filename, Integer idTiers,
            Integer idResponsable) {
        ArrayList<TiersAttachment> outAttachments = new ArrayList<TiersAttachment>();
        List<TiersAttachment> tiersAttachments = null;
        if (idTiers != null)
            tiersAttachments = tiersAttachmentRepository.findByTiersId(idTiers);
        if (idResponsable != null)
            tiersAttachments = tiersAttachmentRepository.findByResponsableId(idResponsable);

        if (tiersAttachments != null && tiersAttachments.size() > 0)
            for (TiersAttachment tiersAttachment : tiersAttachments) {
                if (tiersAttachment.getUploadedFile() != null
                        && tiersAttachment.getUploadedFile().getFilename().equals(filename))
                    outAttachments.add(tiersAttachment);
            }

        return outAttachments;
    }
}
