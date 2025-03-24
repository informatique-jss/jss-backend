package com.jss.osiris.modules.osiris.crm.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.crm.model.Comment;

public interface CommentRepository extends QueryCacheCrudRepository<Comment, Integer> {

    List<Comment> findAll(Pageable pageableRequest);

    List<Comment> findAllByPostId(Pageable pageableRequest, Integer postId);
}