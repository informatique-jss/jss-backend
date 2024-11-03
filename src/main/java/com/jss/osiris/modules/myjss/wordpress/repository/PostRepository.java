package com.jss.osiris.modules.myjss.wordpress.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.myjss.wordpress.model.Category;
import com.jss.osiris.modules.myjss.wordpress.model.MyJssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.myjss.wordpress.model.PublishingDepartment;
import com.jss.osiris.modules.myjss.wordpress.model.Serie;
import com.jss.osiris.modules.myjss.wordpress.model.Tag;

public interface PostRepository extends QueryCacheCrudRepository<Post, Integer> {

        @Query("select p from Post p where isCancelled=false")
        List<Post> searchAll(Pageable pageableRequest);

        List<Post> findByMyJssCategoriesAndIsCancelled(MyJssCategory myJssCategory, Boolean isCancelled,
                        Pageable pageableRequest);

        List<Post> findByPostCategoriesAndIsCancelled(Category category, Boolean isCancelled, Pageable pageableRequest);

        Post findBySlugAndIsCancelled(String slug, Boolean isCancelled);

        @Query("select p from Post p where id not in :postFetchedId")
        List<Post> findPostExcludIds(@Param("postFetchedId") List<Integer> postFetchedId);

        @Query("select p.id  from Post p join p.postViews v where v.day >= :oneWeekAgo and :category MEMBER OF p.postCategories  group by p.id order by sum(v.count) desc ")
        List<Integer> findPostTendency(@Param("oneWeekAgo") LocalDate oneWeekAgo,
                        @Param("category") Category category, Pageable pageable);

        List<Post> findByPostCategoriesAndIsCancelledAndDepartments(Category categoryArticle, boolean b,
                        PublishingDepartment department, Pageable pageableRequest);

        List<Post> findByPostSerieAndIsCancelled(Serie serie, boolean b);

        List<Post> findByPostTagsAndIsCancelled(Tag tag, boolean b, Pageable pageableRequest);

}