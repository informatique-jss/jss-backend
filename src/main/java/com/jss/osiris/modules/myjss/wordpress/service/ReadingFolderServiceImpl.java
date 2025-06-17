package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.myjss.wordpress.model.AssoMailPost;
import com.jss.osiris.modules.myjss.wordpress.model.Media;
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

    @Autowired
    AssoMailPostService assoMailPostService;

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
        List<AssoMailPost> assoMailPosts = null;
        if (readingFolder.getId() != null) {
            assoMailPosts = assoMailPostService.getAssoMailPostsByReadingFolder(readingFolder);
            if (!assoMailPosts.isEmpty())
                for (AssoMailPost asso : assoMailPosts) {
                    asso.setReadingFolder(null);
                    assoMailPostService.addOrUpdateAssoMailPost(asso);
                }
            readingFolderRepository.delete(readingFolder);
        }
    }

    @Override
    public ReadingFolder getFirstPostImage(ReadingFolder readingFolder) {
        List<AssoMailPost> assoMailPosts = assoMailPostService.getAssoMailPostsByReadingFolder(readingFolder);
        Media firstMedia = null;

        if (!assoMailPosts.isEmpty()) {
            for (AssoMailPost assoMailPost : assoMailPosts) {
                if (assoMailPost.getPost() != null && assoMailPost.getPost().getMedia() != null) {
                    firstMedia = assoMailPost.getPost().getMedia();
                    readingFolder.setMedia(firstMedia);
                    addOrUpdateReadingFolder(readingFolder);
                    break;
                }
            }
        }
        return readingFolder;
    }
}
