package com.jss.osiris.modules.osiris.crm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.crm.model.Comment;

public interface CommentRepository extends QueryCacheCrudRepository<Comment, Integer> {

    Page<Comment> findAll(Pageable pageable);

    Page<Comment> findAllByPostId(Integer postId, Pageable pageable);

    Page<Comment> findAllByPostIdAndIsDeletedFalseAndParentCommentIsNull(Integer postId, Pageable pageable);
}