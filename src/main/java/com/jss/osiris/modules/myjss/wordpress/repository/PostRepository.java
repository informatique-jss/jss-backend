package com.jss.osiris.modules.myjss.wordpress.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

        List<Post> findByJssCategoriesAndIsCancelled(JssCategory jssCategory, Boolean isCancelled,
                        Pageable pageableRequest);

        List<Post> findByMyJssCategoriesAndIsCancelled(MyJssCategory myJssCategory, Boolean isCancelled,
                        Pageable pageableRequest);

        List<Post> findByPostCategoriesAndIsCancelled(Category category, Boolean isCancelled, Pageable pageableRequest);

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

        @Query("select p from Post p where :categoryArticle MEMBER OF p.postCategories and size(p.jssCategories) > 0 and p.isCancelled = :isCancelled")
        List<Post> findJssCategoryPosts(@Param("categoryArticle") Category categoryArticle,
                        @Param("isCancelled") boolean b,
                        Pageable pageableRequest);

        @Query("select p from Post p where size(p.myJssCategories) > 0 and p.isCancelled = :isCancelled")
        List<Post> findMyJssCategoryPosts(@Param("isCancelled") boolean b,
                        Pageable pageableRequest);

        List<Post> findByPostCategoriesAndIsCancelledAndDepartments(Category categoryArticle, boolean b,
                        PublishingDepartment department, Pageable pageableRequest);

        List<Post> findByPostSerieAndIsCancelled(Serie serie, boolean b);

        List<Post> findByPostTagsAndIsCancelled(Tag tag, boolean b, Pageable pageableRequest);

        List<Post> findByFullAuthorAndIsCancelled(Author author, boolean b, Pageable pageableRequest);

        @Query("select p from Post p where p.isCancelled = false and :jssCategories member of p.jssCategories and p.date>:date")
        List<Post> findNextArticle(@Param("jssCategories") JssCategory jssCategories,
                        @Param("date") LocalDateTime date, Pageable pageableRequest);

        @Query("select p from Post p where p.isCancelled = false and :jssCategories member of p.jssCategories and p.date<:date")
        List<Post> findPreviousArticle(@Param("jssCategories") JssCategory jssCategories,
                        @Param("date") LocalDateTime date, Pageable pageableRequest);

        @Query("select p from Post p "
                        + "where (:myJssCategory is null or :myJssCategory member of p.myJssCategories) and p.isCancelled = false"
                        + " ")
        List<Post> searchPostsByMyJssCategory(@Param("myJssCategory") MyJssCategory myJssCategory,
                        Pageable pageable);

}