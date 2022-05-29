package com.jss.jssbackend.modules.miscellaneous.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.miscellaneous.model.UploadedFile;
import com.jss.jssbackend.modules.miscellaneous.repository.UploadedFileRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UploadedFileServiceImpl implements UploadedFileService {

    @Autowired
    UploadedFileRepository uploadedFileRepository;

    @Override
    public List<UploadedFile> getUploadedFiles() {
        return IterableUtils.toList(uploadedFileRepository.findAll());
    }

    @Override
    public UploadedFile getUploadedFile(Integer id) {
        Optional<UploadedFile> uploadedFile = uploadedFileRepository.findById(id);
        if (!uploadedFile.isEmpty())
            return uploadedFile.get();
        return null;
    }

    @Override
    public UploadedFile createUploadedFile(String filename, String absoluteFilePath)
            throws NoSuchAlgorithmException, IOException {
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setCreationDate(new Date());
        // TODO : à compléter quand le branchement à l'AD sera fait
        // uploadedFile.setCreatedBy("toto");
        uploadedFile.setChecksum(computeChecksumForFile(absoluteFilePath));
        uploadedFile.setFilename(filename);
        uploadedFile.setPath(absoluteFilePath);
        return uploadedFile;
    }

    private String computeChecksumForFile(String absoluteFilePath) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        try (InputStream fis = new FileInputStream(absoluteFilePath)) {
            byte[] buffer = new byte[1024];
            int nread;
            while ((nread = fis.read(buffer)) != -1) {
                md.update(buffer, 0, nread);
            }
        }

        // bytes to hex
        StringBuilder result = new StringBuilder();
        for (byte b : md.digest()) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
