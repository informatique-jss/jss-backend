package com.jss.osiris.modules.myjss.wordpress.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.myjss.wordpress.model.Author;
import com.jss.osiris.modules.myjss.wordpress.model.Category;
import com.jss.osiris.modules.myjss.wordpress.model.JssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.MyJssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.myjss.wordpress.model.PublishingDepartment;
import com.jss.osiris.modules.myjss.wordpress.model.ReadingFolder;
import com.jss.osiris.modules.myjss.wordpress.model.Serie;
import com.jss.osiris.modules.myjss.wordpress.model.Tag;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

public interface PostService {
        public Post addOrUpdatePostFromWordpress(Post post) throws OsirisException;

        public void updateBookmarkPost(Post post, ReadingFolder readingFolder, Responsable responsable);

        public void deleteBookmarkPost(Post post, Responsable responsable);

        public Page<Post> getBookmarkPostsByReadingFolderForCurrentUser(ReadingFolder readingFolder,
                        Responsable responsable,
                        Pageable pageableRequest);

        public Page<Post> getJssCategoryPosts(String searchText, Pageable pageableRequest) throws OsirisException;

        public List<Post> getMyJssCategoryPosts(int page) throws OsirisException;

        public Page<Post> getPostInterview(Pageable pageableRequest) throws OsirisException;

        public Post getPost(Integer id);

        public Page<Post> getPostsPodcast(Pageable pageableRequest) throws OsirisException;

        public Page<Post> getPostsByJssCategory(Pageable pageableRequest, JssCategory jssCategory);

        public Page<Post> getAllPostsByJssCategory(Pageable pageableRequest, JssCategory jssCategory,
                        String searchText, LocalDateTime consultationDate);

        public Page<Post> getAllPostsByTag(Pageable pageableRequest, Tag tag, String searchText,
                        LocalDateTime consultationDate) throws OsirisException;

        public Page<Post> getAllPostsByCategory(Pageable pageableRequest, Category category);

        public Page<Post> getAllPostsByAuthor(Pageable pageableRequest, Author author, String searchText,
                        LocalDateTime consultationDate);

        public Page<Post> getAllPostsBySerie(Pageable pageableRequest, Serie serie, String searchText);

        public Page<Post> getAllPostsByPublishingDepartment(Pageable pageableRequest,
                        PublishingDepartment publishingDepartment, String searchText) throws OsirisException;

        public Page<Post> getAllPostsByIdf(Pageable pageableRequest, String searchText) throws OsirisException;

        public Page<Post> getAllPremiumPosts(String searchText, Pageable pageableRequest) throws OsirisException;

        public Page<Post> getMostSeenPostByJssCatgory(Pageable pageableRequest, JssCategory jssCategory);

        public Page<Post> getMostSeenPostByTag(Pageable pageableRequest, Tag tag);

        public Page<Post> getMostSeenPostByAuthor(Pageable pageableRequest, Author author);

        public Page<Post> getMostSeenPostBySerie(Pageable pageableRequest, Serie serie);

        public Page<Post> getMostSeenPremiumPost(Pageable pageable);

        public Page<Post> getMostSeenPostByPublishingDepartment(Pageable pageableRequest,
                        PublishingDepartment publishingDepartment) throws OsirisException;

        public Page<Post> getMostSeenPostByIdf(Pageable pageableRequest) throws OsirisException;

        public Page<Post> searchPostsByMyJssCategory(String searchTitle, MyJssCategory myJssCategory, Pageable page);

        public Page<Post> searchPostsByCategory(String searchText, Category category,
                        Pageable pageableRequest);

        public Page<Post> getPostsByMyJssCategory(int page, MyJssCategory myJssCategory);

        public List<Post> getFirstPostsByMyJssCategories(MyJssCategory selectedMyJssCategory);

        public Post getPostsBySlug(String slung);

        public List<Post> getPostExcludedId(List<Integer> postFetchedId);

        public void cancelPost(Post post);

        public Page<Post> getJssCategoryPostTendency(String searchText, Pageable pageableRequest)
                        throws OsirisException;

        public List<Post> getMyJssCategoryPostTendency() throws OsirisException;

        public List<Post> getMyJssCategoryPostMostSeen() throws OsirisException;

        public Page<Post> getJssCategoryPostMostSeen(Pageable pageableRequest) throws OsirisException;

        public Page<Post> getJssCategoryStickyPost(Pageable pageableRequest) throws OsirisException;

        public Page<Post> getMyJssCategoryStickyPost(Pageable pageableRequest, MyJssCategory myJssCategory)
                        throws OsirisException;

        public Page<Post> getTopPostByDepartment(Pageable pageableRequest, PublishingDepartment department)
                        throws OsirisException;

        public Page<Post> getTopPostWithDepartment(Pageable pageableRequest)
                        throws OsirisException;

        public List<Post> getPostBySerie(Serie serie);

        public void reindexPosts() throws OsirisException;

        public Page<Post> computeBookmarkedPosts(Page<Post> posts);

        public Page<Post> applyPremiumAndBookmarks(Page<Post> posts);

        public Post applyPremiumAndBookmarks(Post post, String token, String mail, boolean byPassBookmarkComputation);

        public Post getNextPost(Post post);

        public Post getPreviousPost(Post post);
}
