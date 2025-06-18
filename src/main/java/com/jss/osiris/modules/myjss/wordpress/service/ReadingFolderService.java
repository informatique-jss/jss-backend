package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;

import com.jss.osiris.modules.myjss.wordpress.model.ReadingFolder;

public interface ReadingFolderService {
    public ReadingFolder addOrUpdateReadingFolder(
            ReadingFolder readingFolder);

    public List<ReadingFolder> getAvailableReadingFolders();

    public ReadingFolder getReadingFolder(Integer readingFolderId);

    public void deleteReadingFolder(ReadingFolder readingFolder);

    public ReadingFolder initReadingFolderForCurrentUser();

}
