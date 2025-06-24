package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;

import com.jss.osiris.modules.myjss.wordpress.model.ReadingFolder;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

public interface ReadingFolderService {
    public ReadingFolder addOrUpdateReadingFolder(
            ReadingFolder readingFolder, Responsable responsable);

    public List<ReadingFolder> getAvailableReadingFoldersByResponsable(Responsable responsable);

    public ReadingFolder getReadingFolder(Integer readingFolderId);

    public void deleteReadingFolder(ReadingFolder readingFolder);

    public ReadingFolder initReadingFolderForCurrentUser();

}
