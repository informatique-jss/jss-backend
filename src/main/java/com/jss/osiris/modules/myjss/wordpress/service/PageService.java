package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;

import com.jss.osiris.modules.myjss.wordpress.model.Page;

public interface PageService {
        public Page getPage(Integer id);

        public List<Page> getAllPages();

        public Page addOrUpdatePageFromWordpress(Page page);
}
