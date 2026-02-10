package com.jss.osiris.libs;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.parser.PdfImageObject;
import com.jss.osiris.libs.exception.OsirisException;

@Service
public class PdfTools {

    private Float factorCompression = 0.8f;

    public InputStream keepPages(InputStream file, String pageSelection)
            throws OsirisException {
        PdfReader pdfReader = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            file.transferTo(out);
            pdfReader = new PdfReader(new ByteArrayInputStream(out.toByteArray()));
            out.close();
        } catch (Exception | NoClassDefFoundError e) {
        }

        if (pdfReader != null) {
            pdfReader.selectPages(pageSelection);
            out = new ByteArrayOutputStream();
            PdfStamper pdfStamper;
            try {
                pdfStamper = new PdfStamper(pdfReader, out);
                pdfStamper.close();
                pdfReader.close();
                out.close();
            } catch (DocumentException e) {
                throw new OsirisException(e, "Error while spliting PDF");
            } catch (IOException e) {
                throw new OsirisException(e, "Error while writing PDF");
            }
            return new ByteArrayInputStream(out.toByteArray());
        } else {
            try {
                out.close();
            } catch (IOException e2) {
                throw new OsirisException(e2, "Error while writing PDF");
            }
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public InputStream optimizePdf(InputStream file) throws OsirisException {
        PdfReader reader = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            file.transferTo(out);
            reader = new PdfReader(new ByteArrayInputStream(out.toByteArray()));
        } catch (Exception | NoClassDefFoundError e) {
        }

        if (reader != null) {
            int n = reader.getXrefSize();
            PdfObject object;
            PRStream stream;
            // Look for image and manipulate image stream
            for (int i = 0; i < n; i++) {
                object = reader.getPdfObject(i);
                if (object == null || !object.isStream())
                    continue;
                stream = (PRStream) object;
                if (!PdfName.IMAGE.equals(stream.getAsName(PdfName.SUBTYPE)))
                    continue;
                if (!PdfName.DCTDECODE.equals(stream.getAsName(PdfName.FILTER)))
                    continue;
                PdfImageObject image;
                BufferedImage bi = null;
                try {
                    image = new PdfImageObject(stream);
                    bi = image.getBufferedImage();
                } catch (IOException e) {
                    try {
                        out.close();
                    } catch (IOException e2) {
                        throw new OsirisException(e, "Error while writing picture PDF");
                    }
                }
                if (bi == null)
                    continue;
                int width = (int) (bi.getWidth() * factorCompression);
                int height = (int) (bi.getHeight() * factorCompression);
                if (width <= 0 || height <= 0)
                    continue;
                BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                AffineTransform at = AffineTransform.getScaleInstance(factorCompression, factorCompression);
                Graphics2D g = img.createGraphics();
                g.setColor(Color.WHITE);
                g.drawRenderedImage(bi, at);
                ByteArrayOutputStream imgBytes = new ByteArrayOutputStream();
                try {
                    ImageIO.write(img, "JPG", imgBytes);
                } catch (IOException e) {
                    throw new OsirisException(e, "Error while writing picture PDF");
                }
                stream.clear();
                stream.setData(imgBytes.toByteArray(), false, PRStream.NO_COMPRESSION);
                stream.put(PdfName.TYPE, PdfName.XOBJECT);
                stream.put(PdfName.SUBTYPE, PdfName.IMAGE);
                stream.put(PdfName.FILTER, PdfName.DCTDECODE);
                stream.put(PdfName.WIDTH, new PdfNumber(width));
                stream.put(PdfName.HEIGHT, new PdfNumber(height));
                stream.put(PdfName.BITSPERCOMPONENT, new PdfNumber(8));
                stream.put(PdfName.COLORSPACE, PdfName.DEVICERGB);
            }
            reader.removeUnusedObjects();
            // Save altered PDF
            out = new ByteArrayOutputStream();
            PdfStamper stamper;
            try {
                stamper = new PdfStamper(reader, out);
                stamper.setFullCompression();
                stamper.close();
                reader.close();
            } catch (DocumentException e) {
                throw new OsirisException(e, "Error while spliting PDF");
            } catch (IOException e) {
                throw new OsirisException(e, "Error while writing PDF");
            }
            return new ByteArrayInputStream(out.toByteArray());
        } else {
            try {
                out.close();
            } catch (IOException e2) {
                throw new OsirisException(e2, "Error while writing PDF");
            }
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public File mergePdfs(List<File> filesToMerge) throws OsirisException {
        File mergedFile;
        try {
            mergedFile = File.createTempFile("merged_invoice", ".pdf");
        } catch (IOException e) {
            throw new OsirisException(e, "Error creating merging file");
        }
        try (FileOutputStream os = new FileOutputStream(mergedFile)) {
            Document document = new Document();
            PdfCopy copy = new PdfCopy(document, os);
            document.open();

            for (File file : filesToMerge) {
                PdfReader reader = new PdfReader(file.getAbsolutePath());
                int n = reader.getNumberOfPages();
                for (int page = 1; page <= n; page++) {
                    PdfImportedPage importedPage = copy.getImportedPage(reader, page);
                    copy.addPage(importedPage);
                }
                reader.close();
            }
            document.close();
        } catch (Exception e) {
            throw new OsirisException(e, "Error merging");
        }
        return mergedFile;
    }
}
