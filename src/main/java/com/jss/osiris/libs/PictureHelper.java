package com.jss.osiris.libs;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

import jakarta.imageio.ImageIO;

import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;

@Service
public class PictureHelper {
    public String getPictureFileAsBase64String(File file) throws OsirisException {
        BufferedImage img;
        try {
            img = ImageIO.read(file);
        } catch (IOException e) {
            throw new OsirisException(e, "Can't read file");
        }
        return encodeToString(img, "png");
    }

    private String encodeToString(BufferedImage image, String type) {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();

            imageString = Base64.getEncoder().encodeToString(imageBytes);

            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageString;
    }
}
