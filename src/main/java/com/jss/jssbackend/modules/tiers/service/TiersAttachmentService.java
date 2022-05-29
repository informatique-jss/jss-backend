package com.jss.jssbackend.modules.tiers.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.jss.jssbackend.modules.tiers.model.AttachmentType;
import com.jss.jssbackend.modules.tiers.model.Responsable;
import com.jss.jssbackend.modules.tiers.model.Tiers;
import com.jss.jssbackend.modules.tiers.model.TiersAttachment;

import org.springframework.web.multipart.MultipartFile;

public interface TiersAttachmentService {
        public List<TiersAttachment> getTiersAttachments();

        public TiersAttachment getTiersAttachment(Integer id);

        public List<TiersAttachment> addTiersAttachment(MultipartFile file, Tiers tiers, AttachmentType attachmentType,
                        String filename) throws IOException, NoSuchAlgorithmException;

        public List<TiersAttachment> addResponsableAttachment(MultipartFile file, Responsable responsable,
                        AttachmentType attachmentType,
                        String filename) throws IOException, NoSuchAlgorithmException;

        public List<TiersAttachment> getAttachementsByFilenameAndTiers(String filename, Integer idTiers,
                        Integer idResponsable);
}
