package com.jss.osiris.modules.pdfTools.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.DocumentException;

@Service
public class PdfToolsServiceImpl implements PdfToolsService {

    @Override
    public byte[] compressDocument(MultipartFile file) throws DocumentException {
        // PdfWriter writer = PdfWriter.getInstance(document, new
        // FileOutputStream(file));
        // writer.setCompressionLevel(9);

        return null;// document;
    }
}
