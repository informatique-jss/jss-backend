package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.io.InputStream;

import org.springframework.core.io.Resource;

import com.jss.osiris.libs.exception.OsirisException;

public interface StorageFileService {
    public String saveFile(InputStream file, String filename, String path) throws OsirisException;

    public Resource loadFile(String filename) throws OsirisException;

    public void deleteFile(String path);
}
