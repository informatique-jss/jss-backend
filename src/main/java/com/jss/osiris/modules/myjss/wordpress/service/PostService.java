package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;

import org.springframework.data.domain.Page;
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

        public Page<Post> getJssCategoryPosts(Pageable pageableRequest) throws OsirisException;

        public List<Post> getMyJssCategoryPosts(int page) throws OsirisException;

        public Page<Post> getPostInterview(Pageable pageableRequest) throws OsirisException;

        public Post getPost(Integer id);

        public Page<Post> getPostsPodcast(Pageable pageableRequest) throws OsirisException;

        public Page<Post> getPostsByJssCategory(Pageable pageableRequest, JssCategory jssCategory);

        public List<Post> searchPostsByMyJssCategory(String searchTitle, MyJssCategory myJssCategory, Pageable page);

        public List<Post> getPostsByMyJssCategory(int page, MyJssCategory myJssCategory);

        public List<Post> getFirstPostsByMyJssCategories(MyJssCategory selectedMyJssCategory);

        public Post getPostsBySlug(String slung);

        public List<Post> getPostExcludedId(List<Integer> postFetchedId);

        public void cancelPost(Post post);

        public List<Post> getJssCategoryPostTendency() throws OsirisException;

        public List<Post> getMyJssCategoryPostTendency() throws OsirisException;

        public List<Post> getMyJssCategoryPostMostSeen() throws OsirisException;

        public Page<Post> getJssCategoryPostMostSeen(Pageable pageableRequest) throws OsirisException;

        public Page<Post> getJssCategoryStickyPost(Pageable pageableRequest) throws OsirisException;

        public Page<Post> getTopPostByDepartment(Pageable pageableRequest, PublishingDepartment department)
                        throws OsirisException;

        public Page<Post> getTopPostWithDepartment(Pageable pageableRequest)
                        throws OsirisException;

        public List<Post> getPostBySerie(Serie serie);

        public List<Post> getPostsByTag(Integer page, Tag tag);

        public List<Post> getPostsByAuthor(Integer page, Author author);

        public void reindexPosts() throws OsirisException;

        public List<Post> applyPremium(List<Post> posts);

        public Page<Post> applyPremium(Page<Post> posts);

        public Post applyPremium(Post post);

        public Post getNextPost(Post post);

        public Post getPreviousPost(Post post);
}
