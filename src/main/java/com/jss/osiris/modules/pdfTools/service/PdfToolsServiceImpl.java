package com.jss.osiris.modules.pdfTools.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class PdfToolsServiceImpl implements PdfToolsService {

    @Override
    public byte[] compressDocument(MultipartFile file) throws DocumentException, IOException {
        PdfReader reader = new PdfReader(file.getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfStamper stamper = new PdfStamper(reader, outputStream, PdfWriter.VERSION_1_5);
        stamper.getWriter().setCompressionLevel(9);
        stamper.close();
        reader.close();
        return outputStream.toByteArray();
    }
}
