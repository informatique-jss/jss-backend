package com.jss.osiris.modules.miscellaneous.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.modules.miscellaneous.model.UploadedFile;
import com.jss.osiris.modules.miscellaneous.repository.UploadedFileRepository;

@Service
public class UploadedFileServiceImpl implements UploadedFileService {

    @Autowired
    UploadedFileRepository uploadedFileRepository;

    @Autowired
    ActiveDirectoryHelper activeDirectoryHelper;

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
        uploadedFile.setCreatedBy(activeDirectoryHelper.getCurrentUsername());
        uploadedFile.setChecksum(computeChecksumForFile(absoluteFilePath));
        uploadedFile.setFilename(filename);
        uploadedFile.setPath(absoluteFilePath);
        return uploadedFile;
    }

    @Override
    public void deleteUploadedFile(UploadedFile uploadedFile) {
        if (uploadedFile != null) {
            uploadedFileRepository.delete(uploadedFile);
        }
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
