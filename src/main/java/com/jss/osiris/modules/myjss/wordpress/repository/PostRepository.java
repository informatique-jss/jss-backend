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
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;

public interface PostRepository extends QueryCacheCrudRepository<Post, Integer> {

        @Query("SELECT p from Post p join p.assoMailPosts a"
                        + " WHERE p.isCancelled =:isCancelled AND a.mail= :mail ")
        Page<Post> findBookmarkedPostsByMail(@Param("isCancelled") Boolean isCancelled, @Param("mail") Mail mail,
                        Pageable pageableRequest);

        @Query("select p from Post p where p.isCancelled =:isCancelled and p.date<=CURRENT_TIMESTAMP and p.date>:consultationDate and ((:jssCategory IS NOT NULL AND :jssCategory MEMBER OF p.jssCategories) OR (:jssCategory IS NULL AND size(p.jssCategories) > 0))")
        Page<Post> findByJssCategoriesAndIsCancelled(@Param("jssCategory") JssCategory jssCategory,
                        @Param("isCancelled") Boolean isCancelled, LocalDateTime consultationDate,
                        Pageable pageableRequest);

        @Query("select p from Post p where p.isCancelled =:isCancelled AND p.date<=CURRENT_TIMESTAMP and ((:myJssCategory IS NOT NULL AND :myJssCategory MEMBER OF p.myJssCategories) OR (:myJssCategory IS NULL AND size(p.myJssCategories) > 0))")
        Page<Post> findByMyJssCategoriesAndIsCancelled(@Param("myJssCategory") MyJssCategory myJssCategory,
                        @Param("isCancelled") Boolean isCancelled,
                        Pageable pageableRequest);

        @Query("select p from Post p where p.isCancelled =:isCancelled AND p.date<=CURRENT_TIMESTAMP and :category MEMBER OF p.postCategories")
        Page<Post> findByPostCategoriesAndIsCancelled(Category category, Boolean isCancelled, Pageable pageableRequest);

        Post findBySlugAndIsCancelled(String slug, Boolean isCancelled);

        @Query("select p from Post p where id not in :postFetchedId AND p.date<=CURRENT_TIMESTAMP ")
        List<Post> findPostExcludIds(@Param("postFetchedId") List<Integer> postFetchedId);

        @Query("select p.id from Post p join p.postViews v where p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and size(p.jssCategories) > 0 and v.day >= :oneWeekAgo and :category MEMBER OF p.postCategories group by p.id order by sum(v.count) desc ")
        List<Integer> findJssCategoryPostTendency(@Param("oneWeekAgo") LocalDate oneWeekAgo,
                        @Param("category") Category category, Pageable pageable);

        @Query("select p.id from Post p join p.postViews v where p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and size(p.myJssCategories) > 0 and v.day >= :oneWeekAgo group by p.id order by sum(v.count) desc ")
        List<Integer> findMyJssCategoryPostTendency(@Param("oneWeekAgo") LocalDate oneWeekAgo,
                        Pageable pageable);

        @Query("select p.id from Post p join p.postViews v where p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and size(p.myJssCategories) > 0 group by p.id order by sum(v.count) desc ")
        List<Integer> findMyJssCategoryPostMostSeen(Pageable pageable);

        @Query("select p from Post p join p.postViews v where p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and :jssCategory MEMBER OF p.jssCategories group by p.id order by sum(v.count) desc ")
        Page<Post> findMostSeenPostJssCategory(Pageable pageable, @Param("jssCategory") JssCategory jssCategory);

        @Query("select p from Post p join p.postViews v where p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and size(p.jssCategories) > 0 group by p.id order by sum(v.count) desc ")
        Page<Post> findJssCategoryPostMostSeen(Pageable pageable);

        @Query("select p from Post p where p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and size(p.jssCategories) > 0 and p.isSticky=true ")
        Page<Post> findJssCategoryPostSticky(Pageable pageable);

        @Query("select p from Post p where p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and size(p.myJssCategories) > 0 and (:category member of p.myJssCategories) and p.isSticky=true ")
        Page<Post> findMyJssCategoryStickyPost(MyJssCategory category, Pageable pageable);

        @Query("select p from Post p join p.postViews v where p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and :tag MEMBER OF p.postTags and size(p.jssCategories) > 0 group by p.id order by sum(v.count) desc ")
        Page<Post> findMostSeenPostTag(Pageable pageable, @Param("tag") Tag tag);

        @Query("select p from Post p join p.postViews v where p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and p.fullAuthor =:author and size(p.jssCategories) > 0 group by p.id order by sum(v.count) desc ")
        Page<Post> findMostSeenPostAuthor(Pageable pageable, @Param("author") Author author);

        @Query("select p from Post p join p.postViews v where p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and :serie MEMBER OF p.postSerie and size(p.jssCategories) > 0 group by p.id order by sum(v.count) desc ")
        Page<Post> findMostSeenPostSerie(Pageable pageable, @Param("serie") Serie serie);

        @Query("select p from Post p join p.postViews v where :category MEMBER OF p.postCategories AND p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and :publishingDepartment MEMBER OF p.departments and size(p.jssCategories) > 0 group by p.id order by sum(v.count) desc ")
        Page<Post> findMostSeenPostPublishingDepartment(Pageable pageable, Category category,
                        @Param("publishingDepartment") PublishingDepartment publishingDepartment);

        @Query("select p from Post p join p.postViews v where :category MEMBER OF p.postCategories AND p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and size(p.departments)>0 and size(p.jssCategories) > 0 group by p.id order by sum(v.count) desc ")
        Page<Post> findMostSeenPostsIdf(Pageable pageable, Category category);

        @Query("select p from Post p where p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and :category MEMBER OF p.postCategories and size(p.departments) > 0 and size(p.jssCategories) > 0 ")
        Page<Post> findPostsIdf(Category category, Pageable pageable);

        @Query("select p from Post p where p.isCancelled = false and size(p.jssCategories) > 0 and p.isSticky = true AND p.date<=CURRENT_TIMESTAMP ")
        Page<Post> findJssCategoryStickyPost(Pageable pageable);

        @Query("select p from Post p where :categoryArticle MEMBER OF p.postCategories and size(p.jssCategories) > 0 and p.isCancelled = :isCancelled AND p.date<=CURRENT_TIMESTAMP ")
        Page<Post> findJssCategoryPosts(@Param("categoryArticle") Category categoryArticle,
                        @Param("isCancelled") boolean b,
                        Pageable pageableRequest);

        @Query("select p from Post p where size(p.myJssCategories) > 0 and p.isCancelled = :isCancelled AND p.date<=CURRENT_TIMESTAMP ")
        List<Post> findMyJssCategoryPosts(@Param("isCancelled") boolean b,
                        Pageable pageableRequest);

        Page<Post> findByPostCategoriesAndIsCancelledAndDepartments(Category categoryArticle, boolean b,
                        PublishingDepartment department, Pageable pageableRequest);

        List<Post> findByPostSerieAndIsCancelled(Serie serie, boolean b);

        @Query("SELECT p FROM Post p WHERE :tag MEMBER OF p.postTags AND :category MEMBER OF p.postCategories AND p.isCancelled = :b and p.date<=CURRENT_TIMESTAMP and p.date>:consultationDate  and size(p.jssCategories) > 0")
        Page<Post> findByPostTagsAndIsCancelled(Tag tag, Category category, boolean b, LocalDateTime consultationDate,
                        Pageable pageableRequest);

        @Query("SELECT p FROM Post p WHERE p.fullAuthor =:author AND p.isCancelled = :b AND p.date<=CURRENT_TIMESTAMP AND p.date>:consultationDate AND p.date<=CURRENT_TIMESTAMP ")
        Page<Post> findByFullAuthorAndIsCancelled(Author author, boolean b, LocalDateTime consultationDate,
                        Pageable pageableRequest);

        @Query("SELECT p FROM Post p WHERE :serie MEMBER OF p.postSerie AND p.isCancelled = :b AND p.date<=CURRENT_TIMESTAMP ")
        Page<Post> findByPostSerieAndIsCancelled(Serie serie, boolean b, Pageable pageableRequest);

        @Query("select p from Post p where p.isCancelled = :isCancelled AND p.date<=CURRENT_TIMESTAMP and size(p.jssCategories) > 0 and :publishingDepartment MEMBER OF p.departments and :category MEMBER OF p.postCategories")
        Page<Post> findByDepartmentsAndIsCancelled(Category category, PublishingDepartment publishingDepartment,
                        boolean isCancelled,
                        Pageable pageableRequest);

        @Query("select p from Post p where p.isCancelled = false and :jssCategories member of p.jssCategories and p.date>:date AND p.date<=CURRENT_TIMESTAMP ")
        List<Post> findNextArticle(@Param("jssCategories") JssCategory jssCategories,
                        @Param("date") LocalDateTime date, Pageable pageableRequest);

        @Query("select p from Post p where p.isCancelled = false and :jssCategories member of p.jssCategories and p.date<:date AND p.date<=CURRENT_TIMESTAMP ")
        List<Post> findPreviousArticle(@Param("jssCategories") JssCategory jssCategories,
                        @Param("date") LocalDateTime date, Pageable pageableRequest);

        @Query("select p from Post p "
                        + "where (:myJssCategory member of p.myJssCategories) and p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP "
                        + " ")
        List<Post> searchPostsByMyJssCategory(@Param("myJssCategory") MyJssCategory myJssCategory,
                        Pageable pageable);

}