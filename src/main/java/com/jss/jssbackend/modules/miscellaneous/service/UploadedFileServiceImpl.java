package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.miscellaneous.model.UploadedFile;
import com.jss.jssbackend.modules.miscellaneous.repository.UploadedFileRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UploadedFileServiceImpl implements UploadedFileService {

    @Autowired
    UploadedFileRepository uploadedFileRepository;

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
}
