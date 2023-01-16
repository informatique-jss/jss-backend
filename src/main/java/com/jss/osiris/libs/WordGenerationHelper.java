package com.jss.osiris.libs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ooxml.POIXMLRelation;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;

@Service
public class WordGenerationHelper {
    // a method for creating the htmlDoc /word/htmlDoc#.html in the *.docx ZIP
    // archive
    // String id will be htmlDoc#.
    private static MyXWPFHtmlDocument createHtmlDoc(XWPFDocument document, String id) throws OsirisException {
        OPCPackage oPCPackage = document.getPackage();
        PackagePartName partName;
        try {
            partName = PackagingURIHelper.createPartName("/word/" + id + ".html");
        } catch (InvalidFormatException e) {
            throw new OsirisException(e, "Error when generating word");
        }
        PackagePart part = oPCPackage.createPart(partName, "text/html");
        MyXWPFHtmlDocument myXWPFHtmlDocument = new MyXWPFHtmlDocument(part, id);
        document.addRelation(myXWPFHtmlDocument.getId(), new XWPFHtmlRelation(), myXWPFHtmlDocument);
        return myXWPFHtmlDocument;
    }

    public File generateWordFromHtml(String html) throws OsirisException {

        XWPFDocument document = new XWPFDocument();

        MyXWPFHtmlDocument myXWPFHtmlDocument;
        File tempFile;
        FileOutputStream out = null;
        try {
            tempFile = File.createTempFile("word", "html");

            myXWPFHtmlDocument = createHtmlDoc(document, "htmlDoc1");
            myXWPFHtmlDocument.setHtml(myXWPFHtmlDocument.getHtml().replace("<body></body>",
                    "<body>" + html + "</body>"));
            document.getDocument().getBody().addNewAltChunk().setId(myXWPFHtmlDocument.getId());

            out = new FileOutputStream(tempFile);
            document.write(out);
        } catch (IOException e) {
            throw new OsirisException(e, "impossible to create temp file");
        } finally {
            try {
                if (out != null)
                    out.close();
                document.close();
            } catch (IOException e) {
            }
        }
        return tempFile;
    }

    // a wrapper class for the htmlDoc /word/htmlDoc#.html in the *.docx ZIP archive
    // provides methods for manipulating the HTML
    private static class MyXWPFHtmlDocument extends POIXMLDocumentPart {

        private String html;
        private String id;

        private MyXWPFHtmlDocument(PackagePart part, String id) {
            super(part);
            this.html = "<!DOCTYPE html><html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"><style></style><title>HTML import</title></head><body></body>";
            this.id = id;
        }

        private String getId() {
            return id;
        }

        private String getHtml() {
            return html;
        }

        private void setHtml(String html) {
            this.html = html;
        }

        @Override
        protected void commit() throws IOException {
            PackagePart part = getPackagePart();
            OutputStream out = part.getOutputStream();
            Writer writer = new OutputStreamWriter(out, "UTF-8");
            writer.write(html);
            writer.close();
            out.close();
        }

    }

    // the XWPFRelation for /word/htmlDoc#.html
    private final static class XWPFHtmlRelation extends POIXMLRelation {
        private XWPFHtmlRelation() {
            super(
                    "text/html",
                    "http://schemas.openxmlformats.org/officeDocument/2006/relationships/aFChunk",
                    "/word/htmlDoc#.html");
        }
    }
}
