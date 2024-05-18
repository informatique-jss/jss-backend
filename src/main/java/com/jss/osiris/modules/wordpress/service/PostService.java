package com.jss.osiris.modules.wordpress.service;

import java.util.List;

import com.jss.osiris.modules.wordpress.model.Post;

public interface PostService {
        public List<Post> getPosts(int page);

        public List<Post> getPostInterview(int page);

        public List<Post> getPostPodcast(int page);

        public List<Post> getPostsByMyJssCategory(int page, Integer categoryId);
}
