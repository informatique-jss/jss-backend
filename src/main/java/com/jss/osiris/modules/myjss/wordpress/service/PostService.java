package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.myjss.wordpress.model.Author;
import com.jss.osiris.modules.myjss.wordpress.model.JssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.MyJssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.myjss.wordpress.model.PublishingDepartment;
import com.jss.osiris.modules.myjss.wordpress.model.Serie;
import com.jss.osiris.modules.myjss.wordpress.model.Tag;

public interface PostService {
        public Post addOrUpdatePostFromWordpress(Post post) throws OsirisException;

        public List<Post> getPosts(int page) throws OsirisException;

        public List<Post> getPostInterview(int page) throws OsirisException;

        public Post getPost(Integer id);

        public List<Post> getPostPodcast(int page) throws OsirisException;

        public List<Post> getPostsByJssCategory(int page, JssCategory jssCategory);

        public List<Post> searchPostsByMyJssCategory(String searchTitle, MyJssCategory myJssCategory, Pageable page);

        public List<Post> getFirstPostsByMyJssCategories(MyJssCategory selectedMyJssCategory);

        public Post getPostsBySlug(String slung);

        public List<Post> getPostExcludedId(List<Integer> postFetchedId);

        public void cancelPost(Post post);

        public List<Post> getPostTendency() throws OsirisException;

        public List<Post> getPostMostSeen() throws OsirisException;

        public List<Post> getTopPostByDepartment(Integer page, PublishingDepartment department) throws OsirisException;

        public List<Post> getPostBySerie(Serie serie);

        public List<Post> getPostsByTag(Integer page, Tag tag);

        public List<Post> getPostsByAuthor(Integer page, Author author);

        public void reindexPosts() throws OsirisException;

        public List<Post> applyPremium(List<Post> posts);

        public Post applyPremium(Post post);

        public Post getNextPost(Post post);

        public Post getPreviousPost(Post post);
}
