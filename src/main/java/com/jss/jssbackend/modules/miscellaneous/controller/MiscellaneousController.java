package com.jss.jssbackend.modules.miscellaneous.controller;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import com.jss.jssbackend.modules.miscellaneous.model.Attachment;
import com.jss.jssbackend.modules.miscellaneous.model.AttachmentType;
import com.jss.jssbackend.modules.miscellaneous.model.DocumentType;
import com.jss.jssbackend.modules.miscellaneous.model.LegalForm;
import com.jss.jssbackend.modules.miscellaneous.service.AttachmentService;
import com.jss.jssbackend.modules.miscellaneous.service.AttachmentTypeService;
import com.jss.jssbackend.modules.miscellaneous.service.DocumentTypeService;
import com.jss.jssbackend.modules.miscellaneous.service.LegalFormService;
import com.jss.jssbackend.modules.quotation.model.Quotation;
import com.jss.jssbackend.modules.tiers.model.Responsable;
import com.jss.jssbackend.modules.tiers.model.Tiers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class MiscellaneousController {

    private static final String inputEntryPoint = "/miscellaneous";

    private static final Logger logger = LoggerFactory.getLogger(MiscellaneousController.class);

    @Autowired
    AttachmentTypeService attachmentTypeService;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    DocumentTypeService documentTypeService;

    @Autowired
    LegalFormService legalFormService;

    @GetMapping(inputEntryPoint + "/legal-forms")
    public ResponseEntity<List<LegalForm>> getLegalForms() {
        List<LegalForm> legalForms = null;
        try {
            legalForms = legalFormService.getLegalForms();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching legalForm", e);
            return new ResponseEntity<List<LegalForm>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching legalForm", e);
            return new ResponseEntity<List<LegalForm>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<LegalForm>>(legalForms, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/document-types")
    public ResponseEntity<List<DocumentType>> getDocumentTypes() {
        List<DocumentType> documentTypes = null;
        try {
            documentTypes = documentTypeService.getDocumentTypes();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching documentTypes", e);
            return new ResponseEntity<List<DocumentType>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching documentTypes", e);
            return new ResponseEntity<List<DocumentType>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<DocumentType>>(documentTypes, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/attachment-types")
    public ResponseEntity<List<AttachmentType>> getAttachmentTypes() {
        List<AttachmentType> attachmentTypes = null;
        try {
            attachmentTypes = attachmentTypeService.getAttachmentTypes();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching attachmentType", e);
            return new ResponseEntity<List<AttachmentType>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching attachmentType", e);
            return new ResponseEntity<List<AttachmentType>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<AttachmentType>>(attachmentTypes, HttpStatus.OK);
    }

    @PostMapping(inputEntryPoint + "/attachment/upload")
    public ResponseEntity<List<Attachment>> uploadAttachment(@RequestParam MultipartFile file,
            @RequestParam Integer idEntity, @RequestParam String entityType,
            @RequestParam Integer idAttachmentType,
            @RequestParam String filename) {
        try {
            if (idAttachmentType == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            AttachmentType attachmentType = attachmentTypeService.getAttachmentType(idAttachmentType);

            if (attachmentType == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            if (filename == null || filename.equals(""))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            if (idEntity == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            if (entityType == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            if (!entityType.equals(Tiers.class.getSimpleName())
                    && !entityType.equals(Responsable.class.getSimpleName())
                    && !entityType.equals(Quotation.class.getSimpleName()))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            return new ResponseEntity<List<Attachment>>(
                    attachmentService.addAttachment(file, idEntity, entityType, attachmentType, filename),
                    HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Could not upload the file: " + file.getOriginalFilename() + "!", e);
            return new ResponseEntity<List<Attachment>>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping(inputEntryPoint + "/attachment/download")
    public ResponseEntity<byte[]> downloadAttachment(@RequestParam("idAttachment") Integer idAttachment) {
        byte[] data = null;
        HttpHeaders headers = null;
        try {
            Attachment tiersAttachment = attachmentService.getAttachment(idAttachment);

            if (tiersAttachment == null || tiersAttachment.getUploadedFile() == null
                    || tiersAttachment.getUploadedFile().getPath() == null)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            File file = new File(tiersAttachment.getUploadedFile().getPath());

            if (file != null) {
                data = Files.readAllBytes(file.toPath());

                headers = new HttpHeaders();
                headers.add("filename", tiersAttachment.getUploadedFile().getFilename());
                headers.setAccessControlExposeHeaders(Arrays.asList("filename"));
                headers.setContentLength(data.length);

                // Compute content type
                String mimeType = Files.probeContentType(file.toPath());
                if (mimeType == null)
                    mimeType = "application/octet-stream";
                headers.set("content-type", mimeType);

            }
        } catch (Exception e) {
            logger.error("Error when fetching client types", e);
            return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
    }
}