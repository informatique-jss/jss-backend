package com.jss.osiris.modules.myjss.wordpress.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.myjss.wordpress.model.Author;
import com.jss.osiris.modules.myjss.wordpress.model.Category;
import com.jss.osiris.modules.myjss.wordpress.model.JssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.MyJssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.myjss.wordpress.model.PublishingDepartment;
import com.jss.osiris.modules.myjss.wordpress.model.Serie;
import com.jss.osiris.modules.myjss.wordpress.model.Tag;

public interface PostRepository extends QueryCacheCrudRepository<Post, Integer> {

        @Query("select p from Post p where isCancelled=false")
        List<Post> searchAll(Pageable pageableRequest);

        @Query("select p from Post p where p.isCancelled =:isCancelled and ((:jssCategory IS NOT NULL AND :jssCategory MEMBER OF p.jssCategories) OR (:jssCategory IS NULL AND size(p.jssCategories) > 0))")
        Page<Post> findByJssCategoriesAndIsCancelled(@Param("jssCategory") JssCategory jssCategory,
                        @Param("isCancelled") Boolean isCancelled,
                        Pageable pageableRequest);

        @Query("select p from Post p where p.isCancelled =:isCancelled and ((:myJssCategory IS NOT NULL AND :myJssCategory MEMBER OF p.myJssCategories) OR (:myJssCategory IS NULL AND size(p.myJssCategories) > 0))")
        Page<Post> findByMyJssCategoriesAndIsCancelled(@Param("myJssCategory") MyJssCategory myJssCategory,
                        @Param("isCancelled") Boolean isCancelled,
                        Pageable pageableRequest);

        Page<Post> findByPostCategoriesAndIsCancelled(Category category, Boolean isCancelled, Pageable pageableRequest);

        Post findBySlugAndIsCancelled(String slug, Boolean isCancelled);

        @Query("select p from Post p where id not in :postFetchedId")
        List<Post> findPostExcludIds(@Param("postFetchedId") List<Integer> postFetchedId);

        @Query("select p.id from Post p join p.postViews v where p.isCancelled = false and size(p.jssCategories) > 0 and v.day >= :oneWeekAgo and :category MEMBER OF p.postCategories group by p.id order by sum(v.count) desc ")
        List<Integer> findJssCategoryPostTendency(@Param("oneWeekAgo") LocalDate oneWeekAgo,
                        @Param("category") Category category, Pageable pageable);

        @Query("select p.id from Post p join p.postViews v where p.isCancelled = false and size(p.myJssCategories) > 0 and v.day >= :oneWeekAgo group by p.id order by sum(v.count) desc ")
        List<Integer> findMyJssCategoryPostTendency(@Param("oneWeekAgo") LocalDate oneWeekAgo,
                        Pageable pageable);

        @Query("select p.id from Post p join p.postViews v where p.isCancelled = false and size(p.myJssCategories) > 0 group by p.id order by sum(v.count) desc ")
        List<Integer> findMyJssCategoryPostMostSeen(Pageable pageable);

        @Query("select p from Post p join p.postViews v where p.isCancelled = false and :jssCategory MEMBER OF p.jssCategories group by p.id order by sum(v.count) desc ")
        Page<Post> findMostSeenPostJssCategory(Pageable pageable, @Param("jssCategory") JssCategory jssCategory);

        @Query("select p from Post p join p.postViews v where p.isCancelled = false and size(p.jssCategories) > 0 group by p.id order by sum(v.count) desc ")
        Page<Post> findJssCategoryPostMostSeen(Pageable pageable);

        @Query("select p from Post p join p.postViews v where p.isCancelled = false and :tag MEMBER OF p.postTags and size(p.jssCategories) > 0 group by p.id order by sum(v.count) desc ")
        Page<Post> findMostSeenPostTag(Pageable pageable, @Param("tag") Tag tag);

        @Query("select p from Post p join p.postViews v where p.isCancelled = false and p.fullAuthor =:author and size(p.jssCategories) > 0 group by p.id order by sum(v.count) desc ")
        Page<Post> findMostSeenPostAuthor(Pageable pageable, @Param("author") Author author);

        @Query("select p from Post p join p.postViews v where p.isCancelled = false and :serie MEMBER OF p.postSerie and size(p.jssCategories) > 0 group by p.id order by sum(v.count) desc ")
        Page<Post> findMostSeenPostSerie(Pageable pageable, @Param("serie") Serie serie);

        @Query("select p from Post p join p.postViews v where p.isCancelled = false and :publishingDepartment MEMBER OF p.departments and size(p.jssCategories) > 0 group by p.id order by sum(v.count) desc ")
        Page<Post> findMostSeenPostPublishingDepartment(Pageable pageable,
                        @Param("publishingDepartment") PublishingDepartment publishingDepartment);

        @Query("select p from Post p where p.isCancelled = false and size(p.departments) > 0 and size(p.jssCategories) > 0 ")
        Page<Post> findPostsIdf(Pageable pageable);

        @Query("select p from Post p where p.isCancelled = false and size(p.jssCategories) > 0 and p.sticky = true")
        Page<Post> findJssCategoryStickyPost(Pageable pageable);

        @Query("select p from Post p where :categoryArticle MEMBER OF p.postCategories and size(p.jssCategories) > 0 and p.isCancelled = :isCancelled")
        Page<Post> findJssCategoryPosts(@Param("categoryArticle") Category categoryArticle,
                        @Param("isCancelled") boolean b,
                        Pageable pageableRequest);

        @Query("select p from Post p where size(p.myJssCategories) > 0 and p.isCancelled = :isCancelled")
        List<Post> findMyJssCategoryPosts(@Param("isCancelled") boolean b,
                        Pageable pageableRequest);

        Page<Post> findByPostCategoriesAndIsCancelledAndDepartments(Category categoryArticle, boolean b,
                        PublishingDepartment department, Pageable pageableRequest);

        @Query("SELECT p FROM Post p WHERE :category MEMBER OF p.postCategories AND p.isCancelled = :isCancelled AND SIZE(p.departments) > 0")
        Page<Post> findByPostCategoriesWithDepartments(Category category, @Param("isCancelled") boolean isCancelled,
                        Pageable pageableRequest);

        List<Post> findByPostSerieAndIsCancelled(Serie serie, boolean b);

        Page<Post> findByPostTagsAndIsCancelled(Tag tag, boolean b, Pageable pageableRequest);

        Page<Post> findByFullAuthorAndIsCancelled(Author author, boolean b, Pageable pageableRequest);

        Page<Post> findByPostSerieAndIsCancelled(Serie serie, boolean b, Pageable pageableRequest);

        @Query("select p from Post p where p.isCancelled = :isCancelled and size(p.jssCategories) > 0 and :publishingDepartment MEMBER OF p.departments ")
        Page<Post> findByDepartmentsAndIsCancelled(PublishingDepartment publishingDepartment, boolean isCancelled,
                        Pageable pageableRequest);

        @Query("select p from Post p where p.isCancelled = false and :jssCategories member of p.jssCategories and p.date>:date")
        List<Post> findNextArticle(@Param("jssCategories") JssCategory jssCategories,
                        @Param("date") LocalDateTime date, Pageable pageableRequest);

        @Query("select p from Post p where p.isCancelled = false and :jssCategories member of p.jssCategories and p.date<:date")
        List<Post> findPreviousArticle(@Param("jssCategories") JssCategory jssCategories,
                        @Param("date") LocalDateTime date, Pageable pageableRequest);

        @Query("select p from Post p "
                        + "where (:myJssCategory member of p.myJssCategories) and p.isCancelled = false"
                        + " ")
        List<Post> searchPostsByMyJssCategory(@Param("myJssCategory") MyJssCategory myJssCategory,
                        Pageable pageable);
}