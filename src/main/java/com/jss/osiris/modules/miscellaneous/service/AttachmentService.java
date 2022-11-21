package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.AttachmentType;

public interface AttachmentService {
        public List<Attachment> getAttachments();

        public Attachment getAttachment(Integer id);

        public List<Attachment> addAttachment(MultipartFile file, Integer idEntity, String entityType,
                        AttachmentType attachmentType, String filename, Boolean replaceExistingAttachementType)
                        throws OsirisException;

        public void deleteAttachment(Attachment attachment);

        public void disableDocument(Attachment attachment);

}
