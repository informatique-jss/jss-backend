package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.io.InputStream;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.AttachmentType;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.PiecesJointe;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypeDocument;
import com.jss.osiris.modules.osiris.quotation.model.infoGreffe.DocumentAssocieInfogreffe;

public interface AttachmentService {
        public List<Attachment> getAttachments();

        public Attachment getAttachment(Integer id);

        public List<Attachment> addAttachment(MultipartFile file, Integer idEntity, String codeEntity,
                        String entityType,
                        AttachmentType attachmentType, String filename, Boolean replaceExistingAttachementType,
                        String pageSelection, TypeDocument typeDocument)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public List<Attachment> addAttachment(InputStream file, Integer idEntity, String codeEntity, String entityType,
                        AttachmentType attachmentType, String filename, Boolean replaceExistingAttachementType,
                        String description, PiecesJointe piecesJointe, String pageSelection, TypeDocument typeDocument)
                        throws OsirisException, OsirisClientMessageException, OsirisValidationException,
                        OsirisDuplicateException;

        public void deleteAttachment(Attachment attachment);

        public void disableAttachment(Attachment attachment);

        public Attachment addOrUpdateAttachment(Attachment attachment);

        public List<Attachment> sortAttachmentByDateDesc(List<Attachment> attachments);

        public Attachment cloneAttachment(Attachment attachment) throws OsirisException;

        public List<Attachment> findByDocumentAssocieInfogreffe(DocumentAssocieInfogreffe documentAssocieInfogreffe);

        public Attachment cleanAttachmentForDelete(Attachment attachment);
}
