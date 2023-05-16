package com.jss.osiris.modules.pdfTools.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.DocumentException;

public interface PdfToolsService {
    public byte[] compressDocument(MultipartFile file) throws DocumentException, IOException;
}