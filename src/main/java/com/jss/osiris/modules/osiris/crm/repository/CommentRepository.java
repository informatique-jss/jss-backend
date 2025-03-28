package com.jss.osiris.modules.osiris.crm.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.crm.model.Comment;

public interface CommentRepository extends QueryCacheCrudRepository<Comment, Integer> {

        Page<Comment> findAll(Pageable pageable);

        Page<Comment> findAllByPostId(Integer postId, Pageable pageable);

        Page<Comment> findAllByPostIdAndIsDeletedFalseAndParentCommentIsNull(Integer postId, Pageable pageable);

        @Query("SELECT c FROM Comment c JOIN c.post p "
                        + "WHERE (cast(:creationDate as timestamp) < cast('1970-01-02 01:01:00' as timestamp) OR cast(c.creationDate as date) = cast(:creationDate as date)) "
                        + "AND (:isModerated IS NULL OR c.isModerated = :isModerated) "
                        + "AND (:isDeleted IS NULL OR c.isDeleted = :isDeleted) "
                        + "AND (:authorFirstLastName IS NULL OR LOWER(c.authorFirstName) LIKE CONCAT('%',:authorFirstLastName, '%') "
                        + "OR LOWER(c.authorLastName) LIKE CONCAT('%',:authorFirstLastName, '%')) "
                        + "AND (:content IS NULL OR LOWER(c.content) LIKE CONCAT('%',:content, '%'))"
                        + "AND (:postTitle IS NULL OR LOWER(p.titleText) LIKE CONCAT('%',:postTitle, '%'))")
        Page<Comment> findFiltered(
                        @Param("creationDate") LocalDateTime creationDate,
                        @Param("postTitle") String postTitle,
                        @Param("content") String content,
                        @Param("authorFirstLastName") String authorFirstLastName,
                        @Param("isModerated") Boolean isModerated,
                        @Param("isDeleted") Boolean isDeleted,
                        Pageable pageable);
}