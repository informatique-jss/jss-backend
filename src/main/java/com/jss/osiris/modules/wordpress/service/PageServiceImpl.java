package com.jss.osiris.modules.wordpress.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.wordpress.model.Page;

@Service
public class PageServiceImpl implements PageService {

    @Autowired
    WordpressDelegate wordpressDelegate;

    @Autowired
    MediaService mediaService;

    @Autowired
    AuthorService authorService;

    @Override
    @Cacheable(value = "wordpress-pages")
    public List<Page> getAllPages() {
        List<Page> pages = wordpressDelegate.getAllPages();
        List<Page> finalPages = new ArrayList<Page>();

        for (Page childPage : pages) {
            computePage(childPage);
            if (childPage.getParent() > 0)
                for (Page parentPage : pages)
                    if (parentPage.getId().equals(childPage.getParent())) {
                        if (parentPage.getChildrenPages() == null)
                            parentPage.setChildrenPages(new ArrayList<Page>());
                        parentPage.getChildrenPages().add(childPage);
                    }
        }

        for (Page page : pages) {
            if (page.getParent() <= 0)
                finalPages.add(computePage(page));
        }
        return finalPages;
    }

    private Page computePage(Page page) {
        if (page.getFeatured_media() != null && page.getFeatured_media() > 0)
            page.setMedia(mediaService.getMedia(page.getFeatured_media()));
        if (page.getAcf() != null)
            page.setPremium(page.getAcf().isPremium());
        if (page.getAuthor() != null && page.getAuthor() > 0)
            page.setFullAuthor(authorService.getAuthor(page.getAuthor()));
        return page;
    }
}