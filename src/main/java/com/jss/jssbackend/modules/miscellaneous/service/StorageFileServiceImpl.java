package com.jss.jssbackend.modules.miscellaneous.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageFileServiceImpl implements StorageFileService {

    @Value("${upload.file.directory}")
    private String uploadFolder;

    @Override
    public String saveFile(MultipartFile file, String filename) throws IOException {
        if (filename == null || filename.equals(""))
            filename = file.getOriginalFilename();
        Files.copy(file.getInputStream(), Paths.get(uploadFolder).resolve(filename),
                StandardCopyOption.REPLACE_EXISTING);
        return Paths.get(uploadFolder).resolve(filename).normalize().toAbsolutePath().toString();
    }

    @Override
    public Resource loadFile(String filename) throws MalformedURLException {
        // TODO : ranger les fichiers !
        Path file = Paths.get(uploadFolder).resolve(filename);
        Resource resource = new UrlResource(file.toUri());
        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new RuntimeException("Could not read the file!");
        }
    }
}
