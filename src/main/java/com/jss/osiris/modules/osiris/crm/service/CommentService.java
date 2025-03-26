package com.jss.osiris.modules.osiris.crm.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.crm.model.Comment;

public interface CommentService {

    public Page<Comment> getComments(Pageable pageableRequest);

    public Page<Comment> getParentCommentsForPost(Pageable pageableRequest, Integer postId);

    public Comment getComment(Integer id);

    public Comment addOrUpdateComment(Comment comment) throws OsirisException;

    public Boolean delete(Integer commentId);
}
