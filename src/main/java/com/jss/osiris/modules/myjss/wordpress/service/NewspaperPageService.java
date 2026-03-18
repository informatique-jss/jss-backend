package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;

import com.jss.osiris.libs.search.model.IndexEntity;
import com.jss.osiris.modules.myjss.wordpress.model.NewspaperPage;

public interface NewspaperPageService {

    public NewspaperPage addOrUpdateNewspaperPage(NewspaperPage newspaperPage);

    public List<NewspaperPage> getNewspaperPages();

    public List<IndexEntity> searchNewspapersEntities(String searchText, String sortBy);
}
