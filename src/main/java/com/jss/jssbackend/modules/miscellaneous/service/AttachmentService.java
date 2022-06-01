package com.jss.jssbackend.modules.miscellaneous.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.jss.jssbackend.modules.miscellaneous.model.Attachment;
import com.jss.jssbackend.modules.miscellaneous.model.AttachmentType;

import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {
        public List<Attachment> getAttachments();

        public Attachment getAttachment(Integer id);

        public List<Attachment> addAttachment(MultipartFile file, Integer idEntity, String entityType,
                        AttachmentType attachmentType, String filename) throws IOException, NoSuchAlgorithmException;

}
