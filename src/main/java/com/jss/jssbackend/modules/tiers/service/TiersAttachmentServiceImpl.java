package com.jss.jssbackend.modules.tiers.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.miscellaneous.model.UploadedFile;
import com.jss.jssbackend.modules.miscellaneous.service.StorageFileService;
import com.jss.jssbackend.modules.miscellaneous.service.UploadedFileService;
import com.jss.jssbackend.modules.tiers.model.AttachmentType;
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
            String filename) throws IOException {
        String absoluteFilePath = storageFileService.saveFile(file, filename);

        TiersAttachment tiersAttachment = new TiersAttachment();
        tiersAttachment.setAttachmentType(attachmentType);

        UploadedFile uploadedFile = new UploadedFile();
        // uploadedFile.setChecksum(checksum);
        // TODO : à compléter quand le branchement à l'AD sera fait
        // uploadedFile.setCreatedBy("toto");
        uploadedFile.setCreationDate(new Date());
        uploadedFile.setFilename(filename);
        uploadedFile.setPath(absoluteFilePath);
        tiersAttachment.setUploadedFile(uploadedFile);
        tiersAttachment.setTiers(tiers);
        tiersAttachmentRepository.save(tiersAttachment);

        return tiersAttachmentRepository.findByTiers(tiers);
    }
}
