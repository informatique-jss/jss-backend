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
     * @throws WriterException
     * @throws IOException
     */
    public byte[] getQrCode(String data, Integer size) throws WriterException, IOException {
        // encode
        BitMatrix bitMatrix = new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, size, size);

        String imageFormat = "png";

        // write in a file
        ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();
        MatrixToImageConfig conf = new MatrixToImageConfig(0xff000000, 0xfffefef8);
        MatrixToImageWriter.writeToStream(bitMatrix, imageFormat, fileOutputStream, conf);
        fileOutputStream.close();

        return fileOutputStream.toByteArray();
    }
}
