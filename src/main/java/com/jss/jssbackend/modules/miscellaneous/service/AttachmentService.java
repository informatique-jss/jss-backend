package com.jss.jssbackend.modules.miscellaneous.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.jss.jssbackend.modules.miscellaneous.model.Attachment;
import com.jss.jssbackend.modules.miscellaneous.model.AttachmentType;

public interface AttachmentService {
        public List<Attachment> getAttachments();

        public Attachment getAttachment(Integer id);

        public List<Attachment> addAttachment(MultipartFile file, Integer idEntity, String entityType,
                        AttachmentType attachmentType, String filename, Boolean replaceExistingAttachementType)
                        throws IOException, NoSuchAlgorithmException;

        public void deleteAttachment(Attachment attachment);

        public void disableDocument(Attachment attachment);

}
