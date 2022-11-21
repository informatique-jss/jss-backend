package com.jss.osiris.modules.miscellaneous.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.jss.osiris.libs.exception.OsirisException;

public interface StorageFileService {
    public String saveFile(MultipartFile file, String filename, String path) throws OsirisException;

    public Resource loadFile(String filename) throws OsirisException;

    public void deleteFile(String path);
}
