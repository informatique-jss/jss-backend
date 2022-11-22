package com.jss.osiris.modules.miscellaneous.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.exception.OsirisException;
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
        if (uploadedFile.isPresent())
            return uploadedFile.get();
        return null;
    }

    @Override
    public UploadedFile createUploadedFile(String filename, String absoluteFilePath) throws OsirisException {
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setCreationDate(LocalDateTime.now());
        uploadedFile.setCreatedBy(activeDirectoryHelper.getCurrentUsername());
        uploadedFile.setChecksum(computeChecksumForFile(absoluteFilePath));
        uploadedFile.setFilename(filename);
        uploadedFile.setPath(absoluteFilePath);
        uploadedFileRepository.save(uploadedFile);
        return uploadedFile;
    }

    @Override
    public void deleteUploadedFile(UploadedFile uploadedFile) {
        if (uploadedFile != null) {
            uploadedFileRepository.delete(uploadedFile);
        }
    }

    private String computeChecksumForFile(String absoluteFilePath) throws OsirisException {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new OsirisException("Impossible to find MDR5 algorithm");
        }
        try {
            InputStream fis = new FileInputStream(absoluteFilePath);
            byte[] buffer = new byte[1024];
            int nread;
            while ((nread = fis.read(buffer)) != -1) {
                md.update(buffer, 0, nread);
            }
            fis.close();
        } catch (IOException e) {
            throw new OsirisException("Impossible to find file to compute");
        }

        // bytes to hex
        StringBuilder result = new StringBuilder();
        for (byte b : md.digest()) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
