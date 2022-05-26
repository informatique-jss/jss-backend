package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.List;

import com.jss.jssbackend.modules.miscellaneous.model.UploadedFile;

public interface UploadedFileService {
    public List<UploadedFile> getUploadedFiles();

    public UploadedFile getUploadedFile(Integer id);
}
