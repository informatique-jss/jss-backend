package com.jss.osiris.modules.wordpress.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.wordpress.model.MyJssCategory;
import com.jss.osiris.modules.wordpress.model.Post;
import com.jss.osiris.modules.wordpress.model.PublishingDepartment;
import com.jss.osiris.modules.wordpress.model.Tag;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    WordpressDelegate wordpressDelegate;

    @Autowired
    MediaService mediaService;

    @Autowired
    AuthorService authorService;

    @Autowired
    MyJssCategoryService myJssCategoryService;

    @Autowired
    PublishingDepartmentService publishingDepartmentService;

    @Autowired
    TagService tagService;

    @Value("${wordpress.category.interview.id}")
    private Integer categoryInterviewId;

    @Value("${wordpress.category.podcast.id}")
    private Integer categoryPodcastId;

    @Override
    @Cacheable(value = "wordpress-posts")
    public List<Post> getPosts(int page) {
        List<Post> posts = wordpressDelegate.getPosts(page, null, null);

        if (posts != null && posts.size() > 0)
            for (Post post : posts)
                computePost(post);

        return posts;
    }

    @Override
    @Cacheable(value = "wordpress-posts-category")
    public List<Post> getPostsByMyJssCategory(int page, Integer myJssCategoryId) {
        List<Post> posts = wordpressDelegate.getPosts(page, myJssCategoryId, null);

        if (posts != null && posts.size() > 0)
            for (Post post : posts)
                computePost(post);

        return posts;
    }

    @Override
    @Cacheable(value = "wordpress-posts-interview")
    public List<Post> getPostInterview(int page) {
        List<Post> posts = wordpressDelegate.getPosts(page, null, categoryInterviewId);

        if (posts != null && posts.size() > 0)
            for (Post post : posts)
                computePost(post);

        return posts;
    }

    @Override
    @Cacheable(value = "wordpress-posts-podcast")
    public List<Post> getPostPodcast(int page) {
        List<Post> posts = wordpressDelegate.getPosts(page, null, categoryPodcastId);

        if (posts != null && posts.size() > 0)
            for (Post post : posts)
                computePost(post);

        return posts;
    }

    private Post computePost(Post post) {
        if (post.getFeatured_media() != null && post.getFeatured_media() > 0)
            post.setMedia(mediaService.getMedia(post.getFeatured_media()));
        if (post.getAcf() != null)
            post.setPremium(post.getAcf().isPremium());
        if (post.getAuthor() != null && post.getAuthor() > 0)
            post.setFullAuthor(authorService.getAuthor(post.getAuthor()));
        if (post.getMyjss_category() != null && post.getMyjss_category().length > 0) {
            List<MyJssCategory> categories = new ArrayList<MyJssCategory>();
            List<MyJssCategory> availableCategories = myJssCategoryService.getAvailableMyJssCategories();
            for (Integer i : post.getMyjss_category()) {
                for (MyJssCategory availableCategory : availableCategories) {
                    if (availableCategory.getId().equals(i)) {
                        categories.add(availableCategory);
                        break;
                    }
                }
            }
            post.setFullCategories(categories);
        }
        if (post.getDepartement() != null && post.getDepartement().length > 0) {
            List<PublishingDepartment> departments = new ArrayList<PublishingDepartment>();
            List<PublishingDepartment> availableDepartments = publishingDepartmentService.getAvailableDepartments();
            for (Integer i : post.getDepartement()) {
                for (PublishingDepartment department : availableDepartments) {
                    if (department.getId().equals(i)) {
                        departments.add(department);
                        break;
                    }
                }
            }
            post.setFullDepartment(departments);
        }
        if (post.getTags() != null && post.getTags().length > 0) {
            List<Tag> tags = new ArrayList<Tag>();
            List<Tag> availableTags = tagService.getAvailableTags();
            for (Integer i : post.getTags()) {
                for (Tag tag : availableTags) {
                    if (tag.getId().equals(i)) {
                        tags.add(tag);
                        break;
                    }
                }
            }
            post.setFullTags(tags);
        }
        return post;
    }
}
