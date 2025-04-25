package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.model.UploadedFile;

public interface UploadedFileService {
    public List<UploadedFile> getUploadedFiles();

    public UploadedFile getUploadedFile(Integer id);

    public UploadedFile createUploadedFile(String filename, String absoluteFilePath) throws OsirisException;

    public Boolean definitivelyDeleteUploadedFile(UploadedFile uploadedFile);
}
