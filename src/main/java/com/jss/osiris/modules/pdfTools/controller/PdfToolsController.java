package com.jss.osiris.modules.pdfTools.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.HttpStatus;

import com.itextpdf.text.DocumentException;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.AttachmentType;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.model.Provider;
import com.jss.osiris.modules.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.miscellaneous.service.AttachmentTypeService;
import com.jss.osiris.modules.pdfTools.service.PdfToolsService;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;

@RestController
public class PdfToolsController {

  @Autowired
  PdfToolsService pdfToolsService;

  @Autowired
  AttachmentTypeService attachmentTypeService;

  @Autowired
  AttachmentService attachmentService;

  private static final String inputEntryPoint = "/pdf-tools";

  @PostMapping(inputEntryPoint + "/attachment/upload")
  public ResponseEntity<Resource> sendPdf(@RequestParam("file") MultipartFile file)
      throws IOException, DocumentException {
    byte[] compressedPdf = pdfToolsService.compressDocument(file);
    ByteArrayResource resource = new ByteArrayResource(compressedPdf);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentDispositionFormData("attachment",
        file.getOriginalFilename() + "compressed.pdf");
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentLength(compressedPdf.length);
    return new ResponseEntity<>(resource, headers, HttpStatus.OK);
  }
  /*
   * @PostMapping(inputEntryPoint + "/attachment/upload")
   * public ResponseEntity<List<Attachment>> uploadAttachment(@RequestParam
   * MultipartFile file,
   * 
   * @RequestParam Integer idEntity, @RequestParam String entityType,
   * 
   * @RequestParam Integer idAttachmentType,
   * 
   * @RequestParam String filename, @RequestParam Boolean
   * replaceExistingAttachementType)
   * throws OsirisValidationException, OsirisException,
   * OsirisClientMessageException {
   * if (idAttachmentType == null)
   * throw new OsirisValidationException("idAttachmentType");
   * 
   * AttachmentType attachmentType =
   * attachmentTypeService.getAttachmentType(idAttachmentType);
   * 
   * if (attachmentType == null)
   * throw new OsirisValidationException("attachmentType");
   * 
   * if (filename == null || filename.equals(""))
   * throw new OsirisValidationException("filename");
   * 
   * if (idEntity == null)
   * throw new OsirisValidationException("idEntity");
   * 
   * if (entityType == null)
   * throw new OsirisValidationException("entityType");
   * 
   * if (!entityType.equals(Tiers.class.getSimpleName())
   * && !entityType.equals("Ofx")
   * && !entityType.equals(Responsable.class.getSimpleName())
   * && !entityType.equals(Quotation.class.getSimpleName())
   * && !entityType.equals(CustomerOrder.class.getSimpleName())
   * && !entityType.equals(Provider.class.getSimpleName())
   * && !entityType.equals(CompetentAuthority.class.getSimpleName())
   * && !entityType.equals(Provision.class.getSimpleName())
   * && !entityType.equals(Invoice.class.getSimpleName()))
   * throw new OsirisValidationException("entityType");
   * 
   * return new ResponseEntity<List<Attachment>>(
   * attachmentService.addAttachment(file, idEntity, entityType, attachmentType,
   * filename,
   * replaceExistingAttachementType),
   * HttpStatus.OK);
   * }
   */
}
