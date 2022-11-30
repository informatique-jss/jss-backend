package com.jss.osiris.modules.miscellaneous.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;

@Service
public class StorageFileServiceImpl implements StorageFileService {

    @Value("${upload.file.directory}")
    private String uploadFolder;

    @Override
    public String saveFile(InputStream file, String filename, String path) throws OsirisException {
        if (filename == null || filename.equals(""))
            throw new OsirisException("No filename provided");

        try {
            Files.createDirectories(Paths.get(uploadFolder.trim() + File.separator + path));
        } catch (IOException e) {
            throw new OsirisException("Impossible to create folder");
        }
        try {
            Files.copy(file, Paths.get(uploadFolder.trim() + File.separator + path).resolve(filename),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new OsirisException("Impossible to create file");
        }
        return Paths.get(uploadFolder.trim() + File.separator + path).resolve(filename).normalize().toAbsolutePath()
                .toString();
    }

    @Override
    public void deleteFile(String path) {
        if (path != null && Files.exists(Paths.get(path), LinkOption.NOFOLLOW_LINKS)) {
            File file = new File(path);
            file.delete();
        }
    }

    @Override
    public Resource loadFile(String filename) throws OsirisException {
        Path file = Paths.get(uploadFolder.trim()).resolve(filename);
        Resource resource;
        try {
            resource = new UrlResource(file.toUri());
        } catch (MalformedURLException e) {
            throw new OsirisException("URI File not found");
        }
        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new RuntimeException("Could not read the file!");
        }
    }
}
