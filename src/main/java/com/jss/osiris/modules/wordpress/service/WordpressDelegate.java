package com.jss.osiris.modules.wordpress.service;

import java.util.List;

import com.jss.osiris.modules.wordpress.model.Author;
import com.jss.osiris.modules.wordpress.model.Category;
import com.jss.osiris.modules.wordpress.model.Media;
import com.jss.osiris.modules.wordpress.model.MyJssCategory;
import com.jss.osiris.modules.wordpress.model.Page;
import com.jss.osiris.modules.wordpress.model.Post;
import com.jss.osiris.modules.wordpress.model.PublishingDepartment;
import com.jss.osiris.modules.wordpress.model.Tag;

public interface WordpressDelegate {
    public List<PublishingDepartment> getAvailableDepartments();

    public List<MyJssCategory> getAvailableMyJssCategories();

    public List<Category> getAvailableCategories();

    public List<Tag> getAvailableTags();

    public Media getMedia(Integer id);

    public Author getAuthor(Integer id);

    public List<Page> getAllPages();

    public List<Post> getPosts(int page, Integer myJssCategoryId, Integer categoryId);
}
