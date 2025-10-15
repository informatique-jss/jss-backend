package com.jss.osiris.modules.myjss.wordpress.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.myjss.wordpress.model.PostView;

public interface PostViewRepository extends QueryCacheCrudRepository<PostView, Integer> {

    PostView findByPostAndDay(Post post, LocalDate now);

    @Modifying
    @Query(nativeQuery = true, value = "" +
            " merge into post_view p using (select :idPost as id) t on (p.id_post = t.id and p.day = to_date(:dateToIncrement,'YYYY-MM-DD')) "
            +
            " when matched then update set count = count+1 " +
            " when not matched then insert values (nextval('post_view_sequence'), 1,  to_date(:dateToIncrement,'YYYY-MM-DD'),:idPost) ")
    void incrementPostViewDay(@Param("idPost") Integer idPost, @Param("dateToIncrement") String dateToIncrement);

}