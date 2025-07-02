package com.jss.osiris.modules.myjss.wordpress.service;

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
    public ReadingFolder addOrUpdateReadingFolder(ReadingFolder readingFolder, Responsable responsable) {
        readingFolder.setMail(responsable.getMail());
        return readingFolderRepository.save(readingFolder);
    }

    @Override
    public List<ReadingFolder> getAvailableReadingFoldersByResponsable(Responsable responsable) {
        if (responsable.getMail() != null)
            return readingFolderRepository.findByMail(responsable.getMail());
        return null;
    }

    @Override
    public ReadingFolder getReadingFolder(Integer readingFolderId) {
        Optional<ReadingFolder> readingFolder = readingFolderRepository.findById(readingFolderId);
        if (readingFolder.isPresent())
            return readingFolder.get();
        return null;
    }

    @Override
    public void deleteReadingFolder(ReadingFolder readingFolder) {
        readingFolderRepository.delete(readingFolder);

    }

    @Override
    public ReadingFolder initReadingFolderForCurrentUser() {
        Responsable responsable = employeeService.getCurrentMyJssUser();
        ReadingFolder readingFolder = null;

        if (responsable != null && responsable.getMail() != null) {
            if (this.getAvailableReadingFoldersByResponsable(responsable) == null
                    || this.getAvailableReadingFoldersByResponsable(responsable).size() == 0) {
                readingFolder = new ReadingFolder();
                readingFolder.setLabel("Mes favoris");
                readingFolder.setMail(responsable.getMail());
                return addOrUpdateReadingFolder(readingFolder, responsable);
            } else
                return this.getAvailableReadingFoldersByResponsable(responsable).get(0);
        }
        return readingFolder;
    }
}
