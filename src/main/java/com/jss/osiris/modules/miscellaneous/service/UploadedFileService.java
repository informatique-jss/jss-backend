package com.jss.osiris.modules.miscellaneous.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.UploadedFile;

public interface UploadedFileService {
    public List<UploadedFile> getUploadedFiles();

    public UploadedFile getUploadedFile(Integer id);

    public UploadedFile createUploadedFile(String filename, String absoluteFilePath)
            throws NoSuchAlgorithmException, IOException;

    public void deleteUploadedFile(UploadedFile uploadedFile);
}
