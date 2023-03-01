package com.jss.osiris.libs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.jss.osiris.libs.exception.OsirisException;

@Service
public class QrCodeHelper {

    /**
     * Generate QR code and return it as byte[]
     * Format : png
     * Size in pixel
     * 
     * @param data
     * @param size
     * @return
     * @throws OsirisException
     */
    public byte[] getQrCode(String data, Integer size) throws OsirisException {
        // encode
        BitMatrix bitMatrix;
        try {
            bitMatrix = new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, size, size);
        } catch (WriterException e) {
            throw new OsirisException(e, "Unable to generate QR Code for value " + data);
        }

        String imageFormat = "png";

        // write in a file
        ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();
        MatrixToImageConfig conf = new MatrixToImageConfig(0xff000000, 0x00fefef8);
        try {
            MatrixToImageWriter.writeToStream(bitMatrix, imageFormat, fileOutputStream, conf);
            fileOutputStream.close();
        } catch (IOException e) {
            throw new OsirisException(e, "Unable to write QR Code in memory for value " + data);
        }

        return fileOutputStream.toByteArray();
    }
}
