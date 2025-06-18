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
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSmartCopy;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.parser.PdfImageObject;
import com.jss.osiris.libs.exception.OsirisException;

@Service
public class PdfTools {

    private Float factorCompression = 0.8f;

    private static final int MAX_PAGES = 5;

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

    public void extractFirstPagesOfAllPdfs(Path sourceRoot, Path targetRoot) throws IOException {
        if (!Files.exists(sourceRoot) || !Files.isDirectory(sourceRoot)) {
            throw new IllegalArgumentException("Source path is invalid: " + sourceRoot);
        }

        System.out.println("Beginning extraction...");

        Files.walk(sourceRoot)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().toLowerCase().endsWith(".pdf"))
                .forEach(pdfPath -> {
                    try {
                        Path relativePath = sourceRoot.relativize(pdfPath);
                        Path targetDir = targetRoot.resolve(relativePath.getParent());
                        Files.createDirectories(targetDir);

                        String originalFileName = pdfPath.getFileName().toString();
                        String baseName = originalFileName.substring(0, originalFileName.length() - 4); // remove ".pdf"
                        String targetFileName = baseName + "_ext.pdf";

                        Path outputPath = targetDir.resolve(targetFileName);
                        extractFirstPages(pdfPath.toFile(), outputPath.toFile(), MAX_PAGES);

                        System.out.println("[OK] Extracted: " + pdfPath + " → " + outputPath);
                    } catch (Exception e) {
                        System.err.println("[ERROR] Error processing " + pdfPath + ": " + e.getMessage());
                    }
                });

        System.out.println("Extraction finished");
    }

    private void extractFirstPages(File inputPdf, File outputPdf, int maxPages) throws Exception {
        PdfReader reader = null;
        Document document = null;
        PdfSmartCopy copy = null;

        try {
            reader = new PdfReader(inputPdf.getAbsolutePath());
            int totalPages = reader.getNumberOfPages();
            int pagesToCopy = Math.min(maxPages, totalPages);

            document = new Document(reader.getPageSizeWithRotation(1));
            copy = new PdfSmartCopy(document, new FileOutputStream(outputPdf));
            document.open();

            for (int i = 1; i <= pagesToCopy; i++) {
                copy.addPage(copy.getImportedPage(reader, i));
            }

        } finally {
            if (document != null)
                document.close();
            if (reader != null)
                reader.close();
        }
    }

    private static final String IMAGE_FORMAT = "jpg"; // ou "png"
    private static final float DPI = 200f;

    public void extractFirstPagesAsImages(Path sourceRoot, Path targetRoot) throws IOException {
        if (!Files.exists(sourceRoot) || !Files.isDirectory(sourceRoot)) {
            throw new IllegalArgumentException("Source path is invalid: " + sourceRoot);
        }

        System.out.println("Beginning image extraction...");

        Files.walk(sourceRoot)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().toLowerCase().endsWith(".pdf"))
                .forEach(pdfPath -> {
                    try {
                        Path relativePath = sourceRoot.relativize(pdfPath);
                        Path targetDir = targetRoot.resolve(relativePath.getParent());
                        Files.createDirectories(targetDir);

                        String originalFileName = pdfPath.getFileName().toString();
                        String baseName = originalFileName.substring(0, originalFileName.length() - 4); // remove ".pdf"
                        String imageFileName = baseName + "." + IMAGE_FORMAT;

                        Path outputImagePath = targetDir.resolve(imageFileName);
                        extractFirstPageAsImage(pdfPath.toFile(), outputImagePath.toFile());

                        System.out.println("[OK] Image created: " + outputImagePath);
                    } catch (Exception e) {
                        System.err.println("[ERROR] Failed to extract image from " + pdfPath + ": " + e.getMessage());
                    }
                });

        System.out.println("Image extraction finished.");
    }

    private void extractFirstPageAsImage(File inputPdf, File outputImage) throws IOException {
        try (PDDocument document = PDDocument.load(inputPdf)) {
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage image = renderer.renderImageWithDPI(0, DPI); // first page, 200 DPI
            ImageIO.write(image, IMAGE_FORMAT, outputImage);
        }
    }
}