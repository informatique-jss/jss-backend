package com.jss.osiris.modules.pdfTools.controller;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.HttpStatus;

import com.itextpdf.text.DocumentException;

import com.jss.osiris.modules.pdfTools.service.PdfToolsService;

@RestController
public class PdfToolsController {

  @Autowired
  PdfToolsService pdfToolsService;

  private static final String inputEntryPoint = "/pdf-tools";

  @PostMapping(value = inputEntryPoint, produces = MediaType.APPLICATION_PDF_VALUE)
  public ResponseEntity<byte[]> sendPdf(@RequestParam("file") MultipartFile file)
      throws IOException, DocumentException {
    byte[] compressedPdf = pdfToolsService.compressDocument(file);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentDispositionFormData("attachment", "compressed.pdf");
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentLength(compressedPdf.length);
    return new ResponseEntity<byte[]>(compressedPdf, headers, HttpStatus.OK);
  }

}
