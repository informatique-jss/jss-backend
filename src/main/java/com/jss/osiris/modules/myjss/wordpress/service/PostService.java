package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.myjss.wordpress.model.MyJssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.myjss.wordpress.model.PublishingDepartment;
import com.jss.osiris.modules.myjss.wordpress.model.Serie;
import com.jss.osiris.modules.myjss.wordpress.model.Tag;

public interface PostService {
        public Post addOrUpdatePostFromWordpress(Post post);

        public List<Post> getPosts(int page) throws OsirisException;

        public List<Post> getPostInterview(int page) throws OsirisException;

        public Post getPost(Integer id);

        public List<Post> getPostPodcast(int page) throws OsirisException;

        public List<Post> getPostsByMyJssCategory(int page, MyJssCategory myJssCategory);

        public Post getPostsBySlug(String slung);

        public List<Post> getPostExcludedId(List<Integer> postFetchedId);

        public void cancelPost(Post post);

        public List<Post> getPostTendency() throws OsirisException;

        public List<Post> getTopPostByDepartment(Integer page, PublishingDepartment department) throws OsirisException;

        public List<Post> getPostBySerie(Serie serie);

        public List<Post> getPostsByTag(Integer page, Tag tag);
}
