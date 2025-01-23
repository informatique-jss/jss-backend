package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.myjss.wordpress.model.Page;
import com.jss.osiris.modules.myjss.wordpress.repository.PageRepository;

@Service
public class PageServiceImpl implements PageService {

    @Autowired
    MediaService mediaService;

    @Autowired
    AuthorService authorService;

    @Autowired
    PageRepository pageRepository;

    @Override
    public Page getPage(Integer id) {
        Optional<Page> page = pageRepository.findById(id);
        if (page.isPresent())
            return page.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Page addOrUpdatePageFromWordpress(Page page) {
        if (page.getTitle() != null)
            page.setTitleText(page.getTitle().getRendered());
        if (page.getContent() != null)
            page.setContentText(page.getContent().getRendered());
        if (page.getExcerpt() != null)
            page.setExcerptText(page.getExcerpt().getRendered());

        if (page.getParent() != null)
            page.setParentPage(getPage(page.getParent()));

        return pageRepository.save(computePage(page));
    }

    @Override
    public List<Page> getAllPages() {
        return IterableUtils.toList(pageRepository.findAll());
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