package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.myjss.wordpress.model.ReadingFolder;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.repository.ReadingFolderRepository;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

@Service
public class ReadingFolderServiceImpl implements ReadingFolderService {
    @Autowired
    ReadingFolderRepository readingFolderRepository;

    @Autowired
    EmployeeService employeeService;

    @Override
    public ReadingFolder addOrUpdateReadingFolder(ReadingFolder readingFolder) {
        Responsable responsable = employeeService.getCurrentMyJssUser();
        if (responsable != null)
            readingFolder.setMail(responsable.getMail());
        return readingFolderRepository.save(readingFolder);
    }

    @Override
    public List<ReadingFolder> getAvailableReadingFolders() {
        Responsable responsable = employeeService.getCurrentMyJssUser();
        if (responsable != null)
            return readingFolderRepository.findByMail(responsable.getMail());
        return null;
    }

    @Override
    public ReadingFolder getReadingFolder(Integer readingFolderId) {
        Responsable responsable = employeeService.getCurrentMyJssUser();
        if (responsable != null) {
            Optional<ReadingFolder> readingFolder = readingFolderRepository.findById(readingFolderId);
            if (readingFolder.isPresent())
                return readingFolder.get();
        }
        return null;
    }

    @Override
    public void deleteReadingFolder(ReadingFolder readingFolder) {
        Responsable responsable = employeeService.getCurrentMyJssUser();
        if (responsable != null) {
            if (readingFolder.getId() != null
                    && readingFolder.getMail().getId().equals(responsable.getMail().getId())) {
                readingFolderRepository.delete(readingFolder);
            }
        }
    }

    @Override
    public ReadingFolder initReadingFolderForCurrentUser() {
        Responsable responsable = employeeService.getCurrentMyJssUser();
        ReadingFolder readingFolder = null;

        if (responsable != null && responsable.getMail() != null) {
            if (this.getAvailableReadingFolders() == null || this.getAvailableReadingFolders().size() == 0) {
                readingFolder = new ReadingFolder();
                readingFolder.setLabel("Tous les articles");
                readingFolder.setMail(responsable.getMail());
                readingFolder.setPosts(new ArrayList<>());
                readingFolder = addOrUpdateReadingFolder(readingFolder);
            }
        }
        return readingFolder;
    }
}
