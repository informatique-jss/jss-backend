package com.jss.jssbackend.modules.miscellaneous.service;

import java.io.IOException;
import java.net.MalformedURLException;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageFileService {
    public String saveFile(MultipartFile file, String filename) throws IOException;

    public Resource loadFile(String filename) throws MalformedURLException;
}
