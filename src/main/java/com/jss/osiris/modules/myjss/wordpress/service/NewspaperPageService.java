package com.jss.osiris.modules.myjss.wordpress.service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.myjss.wordpress.model.Newspaper;
import com.jss.osiris.modules.myjss.wordpress.model.NewspaperPage;

public interface NewspaperPageService {

    public NewspaperPage addOrUpdateNewspaperPage(NewspaperPage newspaperPage);

    public void fillDbWithNewspaperPages() throws OsirisException;

    public Iterable<NewspaperPage> processPdfAndSavePages(String filePath, Newspaper newspaper) throws OsirisException;
}
