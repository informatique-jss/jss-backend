package com.jss.osiris.modules.miscellaneous.service;

import java.io.InputStream;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.AttachmentType;

public interface AttachmentService {
        public List<Attachment> getAttachments();

        public Attachment getAttachment(Integer id);

        public List<Attachment> addAttachment(MultipartFile file, Integer idEntity, String entityType,
                        AttachmentType attachmentType, String filename, Boolean replaceExistingAttachementType)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public List<Attachment> addAttachment(InputStream file, Integer idEntity, String entityType,
                        AttachmentType attachmentType, String filename, Boolean replaceExistingAttachementType,
                        String description)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public void deleteAttachment(Attachment attachment);

        public void disableDocument(Attachment attachment);

        public Attachment addOrUpdateAttachment(Attachment attachment);

        public List<Attachment> sortAttachmentByDateDesc(List<Attachment> attachments);

        public Attachment cloneAttachment(Attachment attachment) throws OsirisException;

        public List<Attachment> getInvoiceAttachmentOnProvisionToAnalyse() throws OsirisException;

        public List<Attachment> getReceiptAttachmentOnCompetentAuthorityToAnalyse() throws OsirisException;

}
