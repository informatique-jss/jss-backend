package com.jss.osiris.modules.osiris.crm.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.crm.model.Comment;

public interface CommentService {

    public List<Comment> getComments(Pageable pageableRequest);

    public List<Comment> getCommentsForPost(Pageable pageableRequest, Integer postId);

    public Comment getComment(Integer id);

    public Comment addOrUpdateComment(Comment comment) throws OsirisException;

    public Boolean delete(Integer commentId);
}
