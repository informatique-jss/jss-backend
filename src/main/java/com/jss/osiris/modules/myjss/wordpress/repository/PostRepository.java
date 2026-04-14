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
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

import jakarta.persistence.QueryHint;

public interface PostRepository extends QueryCacheCrudRepository<Post, Integer> {

        @Query("""
                        select p from Post p
                        where p.isCancelled =:isCancelled
                        and p.date<=CURRENT_TIMESTAMP
                        and p.date>:consultationDate
                        and (
                                        (:jssCategory IS NOT NULL
                                        AND :jssCategory MEMBER OF p.jssCategories)
                                OR (
                                        :jssCategory IS NULL AND
                                        p.source=:source))""")
        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        Page<Post> findByJssCategoriesAndIsCancelled(@Param("jssCategory") JssCategory jssCategory,
                        @Param("isCancelled") Boolean isCancelled, LocalDateTime consultationDate, String source,
                        Pageable pageableRequest);

        @Query("""
                        SELECT p
                        FROM Post p
                        WHERE p.isCancelled = :isCancelled
                        AND p.date <= CURRENT_TIMESTAMP
                        AND EXISTS (
                                SELECT 1 FROM Post p2
                                JOIN p2.jssCategories c1
                                WHERE p2 = p AND c1 = :categoryToSelect
                        )
                        AND NOT EXISTS (
                                SELECT 1 FROM Post p3
                                JOIN p3.jssCategories c2
                                WHERE p3 = p AND c2 IN :listCategories
                        )

                        """)
        Page<Post> findByJssCategoryAndIsCancelledAndNotInJssCategories(
                        @Param("categoryToSelect") JssCategory categoryToSelect,
                        @Param("isCancelled") Boolean isCancelled,
                        @Param("listCategories") List<JssCategory> listCategories,
                        Pageable pageableRequest);

        @Query("select p from Post p " +
                        "where p.isCancelled = :isCancelled " +
                        "  and p.date <= CURRENT_TIMESTAMP " +
                        "  and ( " +
                        "  (:myJssCategoryId IS NOT NULL AND exists (select 1 from p.myJssCategories c where c.id = :myJssCategoryId)) "
                        + "OR (:myJssCategoryId IS NULL AND p.source=:source))")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        Page<Post> findByMyJssCategoriesAndIsCancelled(
                        @Param("myJssCategoryId") Integer myJssCategoryId,
                        @Param("isCancelled") Boolean isCancelled, String source,
                        Pageable pageableRequest);

        @Query("select p from Post p " +
                        "where p.isCancelled = :isCancelled " +
                        "  and p.date <= CURRENT_TIMESTAMP " +
                        "  and ( " +
                        "  (:jssCategoryId IS NOT NULL AND exists (select 1 from p.jssCategories c where c.id = :jssCategoryId)) "
                        + "OR (:jssCategoryId IS NULL AND p.source=:source))")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        Page<Post> findByJssCategoriesAndIsCancelled(
                        @Param("jssCategoryId") Integer jssCategoryId,
                        @Param("isCancelled") Boolean isCancelled, String source,
                        Pageable pageableRequest);

        @Query("select p from Post p where p.isCancelled =:isCancelled AND p.date<=CURRENT_TIMESTAMP and :category MEMBER OF p.postCategories")
        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        Page<Post> findByPostCategoriesAndIsCancelled(Category category, Boolean isCancelled, Pageable pageableRequest);

        @Query("select p from Post p " +
                        "where p.isCancelled = :isCancelled " +
                        "  and p.date <= CURRENT_TIMESTAMP " +
                        "  and ( " +
                        "  (:myJssCategoryId IS NOT NULL AND exists (select 1 from p.myJssCategories c where c.id = :myJssCategoryId)) "
                        + "OR (:myJssCategoryId IS NULL AND p.source=:source))" +
                        "  and ( " +
                        "  (:categoryId IS NOT NULL AND exists (select 1 from p.postCategories c where c.id = :categoryId)) "
                        + "OR (:categoryId IS NULL AND size(p.postCategories) > 0))")
        Page<Post> findByPostCategoriesAndMyJssCategoriesAndIsCancelled(Integer categoryId, Integer myJssCategoryId,
                        Boolean isCancelled, String source, Pageable pageableRequest);

        Post findBySlugAndIsCancelled(String slug, Boolean isCancelled);

        @Query("select p from Post p where id not in :postFetchedId AND p.date<=CURRENT_TIMESTAMP and coalesce(isLegacy,false)=false ")
        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        List<Post> findPostExcludIds(@Param("postFetchedId") List<Integer> postFetchedId);

        @Query("select p from Post p join p.postViews v where p.isCancelled = false and p.source=:source and v.day >= :oneWeekAgo and :category MEMBER OF p.postCategories group by p.id order by sum(v.count) desc ")
        Page<Post> findJssCategoryPostTendency(@Param("oneWeekAgo") LocalDate oneWeekAgo,
                        @Param("category") Category category, String source, Pageable pageable);

        @Query("select p.id from Post p join p.postViews v where p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and p.source=:source and v.day >= :oneWeekAgo group by p.id order by sum(v.count) desc ")
        List<Integer> findMyJssCategoryPostTendency(@Param("oneWeekAgo") LocalDate oneWeekAgo, String source,
                        Pageable pageable);

        @Query("select p.id from Post p join p.postViews v where p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and p.source=:source group by p.id order by sum(v.count) desc ")
        List<Integer> findMyJssCategoryPostMostSeen(String source, Pageable pageable);

        @Query("select p from Post p join p.postViews v where p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and :jssCategory MEMBER OF p.jssCategories group by p.id order by sum(v.count) desc ")
        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        Page<Post> findMostSeenPostJssCategory(Pageable pageable, @Param("jssCategory") JssCategory jssCategory);

        @Query("select p from Post p join p.postViews v where p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and p.source=:source group by p.id order by sum(v.count) desc ")
        Page<Post> findJssCategoryPostMostSeen(String source, Pageable pageable);

        @Query("select p from Post p where p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and p.source=:source and p.isSticky=true ")
        Page<Post> findJssCategoryPostSticky(String source, Pageable pageable);

        @Query("select p from Post p where p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and p.source=:source and (:category member of p.myJssCategories) and p.isSticky=true ")
        Page<Post> findMyJssCategoryStickyPost(MyJssCategory category, String source, Pageable pageable);

        @Query("select p from Post p join p.postViews v where p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and :tag MEMBER OF p.postTags and p.source=:source group by p.id order by sum(v.count) desc ")
        Page<Post> findMostSeenPostTag(Pageable pageable, @Param("tag") Tag tag, String source);

        @Query("select p from Post p join p.postViews v where p.isCancelled = false and coalesce(p.isHiddenAuthor,false)=false AND p.date<=CURRENT_TIMESTAMP and p.fullAuthor =:author and p.source=:source group by p.id order by sum(v.count) desc ")
        Page<Post> findMostSeenPostAuthor(Pageable pageable, @Param("author") Author author, String source);

        @Query("select p from Post p join p.postViews v where p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and :serie MEMBER OF p.postSerie and p.source=:source group by p.id order by sum(v.count) desc ")
        Page<Post> findMostSeenPostSerie(Pageable pageable, @Param("serie") Serie serie, String source);

        @Query("select p from Post p join p.postViews v where p.isCancelled = false AND p.isPremium = true AND p.date<=CURRENT_TIMESTAMP and p.source=:source group by p.id order by sum(v.count) desc ")
        Page<Post> findMostSeenPremiumPosts(Pageable pageable, String source);

        @Query("select p from Post p join p.postViews v where :category MEMBER OF p.postCategories AND p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and :publishingDepartment MEMBER OF p.departments and p.source=:source group by p.id order by sum(v.count) desc ")
        Page<Post> findMostSeenPostPublishingDepartment(Pageable pageable, Category category,
                        @Param("publishingDepartment") PublishingDepartment publishingDepartment, String source);

        @Query("select p from Post p join p.postViews v where :category MEMBER OF p.postCategories AND p.isCancelled = false and size(p.departments)>0 and p.source=:source group by p.id order by sum(v.count) desc ")
        Page<Post> findMostSeenPostsIdf(Pageable pageable, Category category, String source);

        @Query("select p from Post p where p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and :category MEMBER OF p.postCategories and size(p.departments) > 0 and p.source=:source ")
        Page<Post> findPostsIdf(Category category, Pageable pageable, String source);

        @Query("select p from Post p where p.isCancelled = false and p.source=:source and p.isSticky = true AND p.date<=CURRENT_TIMESTAMP ")
        Page<Post> findJssCategoryStickyPost(Pageable pageable, String source);

        /* TODO */
        @Query("select p from Post p where :categoryArticle MEMBER OF p.postCategories and p.source=:source and p.isCancelled = :isCancelled AND p.date<=CURRENT_TIMESTAMP order by coalesce(p.isStayOnTop, false) desc, p.date desc")
        Page<Post> findJssCategoryPosts(@Param("categoryArticle") Category categoryArticle,
                        @Param("isCancelled") boolean b, String source,
                        Pageable pageableRequest);

        @Query("select p from Post p where p.source=:source and p.isCancelled = :isCancelled AND p.date<=CURRENT_TIMESTAMP ")
        List<Post> findMyJssCategoryPosts(@Param("isCancelled") boolean b, String source,
                        Pageable pageableRequest);

        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        Page<Post> findByPostCategoriesAndIsCancelledAndDepartments(Category categoryArticle, boolean b,
                        PublishingDepartment department, Pageable pageableRequest);

        @Synchronize("post")
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        List<Post> findByPostSerieAndIsCancelled(Serie serie, boolean b);

        @Query("SELECT p FROM Post p WHERE :tag MEMBER OF p.postTags AND :category MEMBER OF p.postCategories AND p.isCancelled = :b and p.date<=CURRENT_TIMESTAMP and p.date>:consultationDate  and p.source=:source")
        Page<Post> findByPostTagsAndIsCancelled(Tag tag, Category category, boolean b, LocalDateTime consultationDate,
                        String source,
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

        @Query("select p from Post p where p.isCancelled = :isCancelled AND p.date<=CURRENT_TIMESTAMP and  p.source=:source and :publishingDepartment MEMBER OF p.departments and :category MEMBER OF p.postCategories")
        Page<Post> findByDepartmentsAndIsCancelled(Category category, PublishingDepartment publishingDepartment,
                        boolean isCancelled, String source,
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

        @Query("select p from Post p where p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and p.source =:source")
        List<Post> findAllJssPost(@Param("source") String source);

        @Query("select p from Post p where p.isCancelled = false AND p.date<=CURRENT_TIMESTAMP and p.source=:source")
        List<Post> findAllMyJssPost(@Param("source") String source);

        @Query("""
                        SELECT po
                        FROM CustomerOrder co
                        JOIN co.assoAffaireOrders aao
                        JOIN aao.services s
                        JOIN s.provisions p
                        JOIN p.assoProvisionPostNewspapers apn
                        JOIN apn.post po
                        WHERE co.responsable = :responsable
                        AND co.customerOrderStatus.id = 12
                                                                        """)
        List<Post> findAllJssPostPurchasedByResponsable(Responsable responsable);

        Post findByIdAndIsCancelled(Integer id, boolean isCancelled);
}