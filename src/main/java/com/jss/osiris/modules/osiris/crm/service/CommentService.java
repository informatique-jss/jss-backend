package com.jss.osiris.modules.osiris.crm.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.crm.model.Comment;
import com.jss.osiris.modules.osiris.crm.model.CommentSearch;

public interface CommentService {

    public Page<Comment> getComments(CommentSearch commentSearch, Pageable pageableRequest);

    public Page<Comment> getParentCommentsForPost(Pageable pageableRequest, Integer postId);

    public Comment getComment(Integer id);

    public Comment addOrUpdateComment(Comment comment, Integer parentCommentId, Integer postId) throws OsirisException;

    public Boolean delete(Integer commentId);
}
