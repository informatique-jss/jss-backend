package com.jss.osiris.modules.myjss.wordpress.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.Synchronize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.myjss.wordpress.model.Author;
import com.jss.osiris.modules.myjss.wordpress.model.Category;
import com.jss.osiris.modules.myjss.wordpress.model.JssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.MyJssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.myjss.wordpress.model.PublishingDepartment;
import com.jss.osiris.modules.myjss.wordpress.model.ReadingFolder;
import com.jss.osiris.modules.myjss.wordpress.model.Serie;
import com.jss.osiris.modules.myjss.wordpress.model.Tag;

import jakarta.persistence.QueryHint;

public interface PostRepository extends QueryCacheCrudRepository<Post, Integer> {

        @Query("select p from Post p where p.isCancelled =:isCancelled and p.date<=CURRENT_TIMESTAMP and p.date>:consultationDate and ((:jssCategory IS NOT NULL AND :jssCategory MEMBER OF p.jssCategories) OR (:jssCategory IS NULL AND size(p.jssCategories) > 0))")
        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        Page<Post> findByJssCategoriesAndIsCancelled(@Param("jssCategory") JssCategory jssCategory,
                        @Param("isCancelled") Boolean isCancelled, LocalDateTime consultationDate,
                        Pageable pageableRequest);

        @Query("select p from Post p where p.isCancelled =:isCancelled AND p.date<=CURRENT_TIMESTAMP and ((:myJssCategory IS NOT NULL AND :myJssCategory MEMBER OF p.myJssCategories) OR (:myJssCategory IS NULL AND size(p.myJssCategories) > 0))")
        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        Page<Post> findByMyJssCategoriesAndIsCancelled(@Param("myJssCategory") MyJssCategory myJssCategory,
                        @Param("isCancelled") Boolean isCancelled,
                        Pageable pageableRequest);

        @Query("select p from Post p where p.isCancelled =:isCancelled AND p.date<=CURRENT_TIMESTAMP and :category MEMBER OF p.postCategories")
        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        Page<Post> findByPostCategoriesAndIsCancelled(Category category, Boolean isCancelled, Pageable pageableRequest);

        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        Post findBySlugAndIsCancelled(String slug, Boolean isCancelled);

        @Query("select p from Post p where id not in :postFetchedId AND p.date<=CURRENT_TIMESTAMP and coalesce(isLegacy,false)=false ")
        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        List<Post> findPostExcludIds(@Param("postFetchedId") List<Integer> postFetchedId);

        @Query("select p from Post p join p.postViews v where p.isCancelled = false and size(p.jssCategories) > 0 and v.day >= :oneWeekAgo and :category MEMBER OF p.postCategories group by p.id order by sum(v.count) desc ")
        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        Page<Post> findJssCategoryPostTendency(@Param("oneWeekAgo") LocalDate oneWeekAgo,
                        @Param("category") Category category, Pageable pageable);

        @Query("select p.id from Post p join p.postViews v where p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and size(p.myJssCategories) > 0 and v.day >= :oneWeekAgo group by p.id order by sum(v.count) desc ")
        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        List<Integer> findMyJssCategoryPostTendency(@Param("oneWeekAgo") LocalDate oneWeekAgo,
                        Pageable pageable);

        @Query("select p.id from Post p join p.postViews v where p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and size(p.myJssCategories) > 0 group by p.id order by sum(v.count) desc ")
        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        List<Integer> findMyJssCategoryPostMostSeen(Pageable pageable);

        @Query("select p from Post p join p.postViews v where p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and :jssCategory MEMBER OF p.jssCategories group by p.id order by sum(v.count) desc ")
        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        Page<Post> findMostSeenPostJssCategory(Pageable pageable, @Param("jssCategory") JssCategory jssCategory);

        @Query("select p from Post p join p.postViews v where p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and size(p.jssCategories) > 0 group by p.id order by sum(v.count) desc ")
        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        Page<Post> findJssCategoryPostMostSeen(Pageable pageable);

        @Query("select p from Post p where p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and size(p.jssCategories) > 0 and p.isSticky=true ")
        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        Page<Post> findJssCategoryPostSticky(Pageable pageable);

        @Query("select p from Post p where p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and size(p.myJssCategories) > 0 and (:category member of p.myJssCategories) and p.isSticky=true ")
        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        Page<Post> findMyJssCategoryStickyPost(MyJssCategory category, Pageable pageable);

        @Query("select p from Post p join p.postViews v where p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and :tag MEMBER OF p.postTags and size(p.jssCategories) > 0 group by p.id order by sum(v.count) desc ")
        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        Page<Post> findMostSeenPostTag(Pageable pageable, @Param("tag") Tag tag);

        @Query("select p from Post p join p.postViews v where p.isCancelled = false and coalesce(p.isHiddenAuthor,false)=false AND p.date<=CURRENT_TIMESTAMP and p.fullAuthor =:author and size(p.jssCategories) > 0 group by p.id order by sum(v.count) desc ")
        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        Page<Post> findMostSeenPostAuthor(Pageable pageable, @Param("author") Author author);

        @Query("select p from Post p join p.postViews v where p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and :serie MEMBER OF p.postSerie and size(p.jssCategories) > 0 group by p.id order by sum(v.count) desc ")
        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        Page<Post> findMostSeenPostSerie(Pageable pageable, @Param("serie") Serie serie);

        @Query("select p from Post p join p.postViews v where p.isCancelled = false AND p.isPremium = true AND p.date<=CURRENT_TIMESTAMP and size(p.jssCategories) > 0 group by p.id order by sum(v.count) desc ")
        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        Page<Post> findMostSeenPremiumPosts(Pageable pageable);

        @Query("select p from Post p join p.postViews v where :category MEMBER OF p.postCategories AND p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and :publishingDepartment MEMBER OF p.departments and size(p.jssCategories) > 0 group by p.id order by sum(v.count) desc ")
        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        Page<Post> findMostSeenPostPublishingDepartment(Pageable pageable, Category category,
                        @Param("publishingDepartment") PublishingDepartment publishingDepartment);

        @Query("select p from Post p join p.postViews v where :category MEMBER OF p.postCategories AND p.isCancelled = false and size(p.departments)>0 and size(p.jssCategories) > 0 group by p.id order by sum(v.count) desc ")
        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        Page<Post> findMostSeenPostsIdf(Pageable pageable, Category category);

        @Query("select p from Post p where p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and :category MEMBER OF p.postCategories and size(p.departments) > 0 and size(p.jssCategories) > 0 ")
        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        Page<Post> findPostsIdf(Category category, Pageable pageable);

        @Query("select p from Post p where p.isCancelled = false and size(p.jssCategories) > 0 and p.isSticky = true AND p.date<=CURRENT_TIMESTAMP ")
        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        Page<Post> findJssCategoryStickyPost(Pageable pageable);

        @Query("select p from Post p where :categoryArticle MEMBER OF p.postCategories and size(p.jssCategories) > 0 and p.isCancelled = :isCancelled AND p.date<=CURRENT_TIMESTAMP ")
        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        Page<Post> findJssCategoryPosts(@Param("categoryArticle") Category categoryArticle,
                        @Param("isCancelled") boolean b,
                        Pageable pageableRequest);

        @Query("select p from Post p where size(p.myJssCategories) > 0 and p.isCancelled = :isCancelled AND p.date<=CURRENT_TIMESTAMP ")
        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        List<Post> findMyJssCategoryPosts(@Param("isCancelled") boolean b,
                        Pageable pageableRequest);

        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        Page<Post> findByPostCategoriesAndIsCancelledAndDepartments(Category categoryArticle, boolean b,
                        PublishingDepartment department, Pageable pageableRequest);

        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        List<Post> findByPostSerieAndIsCancelled(Serie serie, boolean b);

        @Query("SELECT p FROM Post p WHERE :tag MEMBER OF p.postTags AND :category MEMBER OF p.postCategories AND p.isCancelled = :b and p.date<=CURRENT_TIMESTAMP and p.date>:consultationDate  and size(p.jssCategories) > 0")
        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        Page<Post> findByPostTagsAndIsCancelled(Tag tag, Category category, boolean b, LocalDateTime consultationDate,
                        Pageable pageableRequest);

        @Query("SELECT p FROM Post p WHERE p.fullAuthor =:author AND p.isCancelled = :b and coalesce(p.isHiddenAuthor,false)=false AND p.date<=CURRENT_TIMESTAMP AND p.date>:consultationDate AND p.date<=CURRENT_TIMESTAMP ")
        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        Page<Post> findByFullAuthorAndIsCancelled(Author author, boolean b, LocalDateTime consultationDate,
                        Pageable pageableRequest);

        @Query("SELECT p FROM Post p WHERE :serie MEMBER OF p.postSerie AND p.isCancelled = :b AND p.date<=CURRENT_TIMESTAMP ")
        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        Page<Post> findByPostSerieAndIsCancelled(Serie serie, boolean b, Pageable pageableRequest);

        @Query("select p from Post p where p.isCancelled = :isCancelled AND p.date<=CURRENT_TIMESTAMP and size(p.jssCategories) > 0 and :publishingDepartment MEMBER OF p.departments and :category MEMBER OF p.postCategories")
        Page<Post> findByDepartmentsAndIsCancelled(Category category, PublishingDepartment publishingDepartment,
                        boolean isCancelled,
                        Pageable pageableRequest);

        @Query("select p from Post p where p.isCancelled = false and :jssCategories member of p.jssCategories and p.date>:date AND p.date<=CURRENT_TIMESTAMP ")
        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        List<Post> findNextArticle(@Param("jssCategories") JssCategory jssCategories,
                        @Param("date") LocalDateTime date, Pageable pageableRequest);

        @Query("select p from Post p where p.isCancelled = false and :jssCategories member of p.jssCategories and p.date<:date AND p.date<=CURRENT_TIMESTAMP ")
        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        List<Post> findPreviousArticle(@Param("jssCategories") JssCategory jssCategories,
                        @Param("date") LocalDateTime date, Pageable pageableRequest);

        @Query("select p from Post p "
                        + "where (:myJssCategory member of p.myJssCategories) and p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP "
                        + " ")
        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        List<Post> searchPostsByMyJssCategory(@Param("myJssCategory") MyJssCategory myJssCategory,
                        Pageable pageable);

        @Query("SELECT p FROM Post p WHERE :readingFolder MEMBER OF p.readingFolders")
        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        Page<Post> findByReadingFolders(@Param(value = "readingFolder") ReadingFolder readingFolder,
                        Pageable pageable);

        @Query("SELECT p FROM Post p WHERE (p.isCancelled = false OR p.isCancelled IS NULL) AND p.isPremium = true AND :category MEMBER OF p.postCategories")
        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        Page<Post> findActivePremiumPosts(@Param("category") Category category, Pageable pageable);

        @Query("select p from Post p where p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and size(p.jssCategories) > 0 ")
        List<Post> findAllJssPost();

        @Query("select p from Post p where p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and size(p.myJssCategories) > 0 ")
        List<Post> findAllMyJssPost();
}