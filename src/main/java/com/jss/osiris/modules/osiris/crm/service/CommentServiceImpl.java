package com.jss.osiris.modules.osiris.crm.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.myjss.wordpress.service.PostService;
import com.jss.osiris.modules.osiris.crm.model.Comment;
import com.jss.osiris.modules.osiris.crm.repository.CommentRepository;
import com.jss.osiris.modules.osiris.miscellaneous.service.MailService;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    PostService postService;

    @Autowired
    MailService mailService;

    @Override
    public List<Comment> getComments(Pageable pageableRequest) {
        return IterableUtils.toList(commentRepository.findAll(pageableRequest));
    }

    @Override
    public Comment getComment(Integer id) {
        Optional<Comment> comment = commentRepository.findById(id);
        if (comment.isPresent())
            return comment.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Comment addOrUpdateComment(Comment comment) throws OsirisException {

        // Make sure the mail exists or is created before beeing linked with the comment
        mailService.populateMailId(comment.getMail());

        Post post = postService.getPost(comment.getPost().getId());

        if (post != null) {
            return commentRepository.save(comment);
        }

        throw new OsirisException("Trying to add a comment on a non-existing post");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(Integer commentId) {

        Comment comment = getComment(commentId);

        if (comment != null) {
            comment.setIsDeleted(true);
            commentRepository.save(comment);

            return true;
        }
        return false;
    }

    @Override
    public List<Comment> getCommentsForPost(Pageable pageableRequest, Integer postId) {
        Post post = postService.getPost(postId);

        if (post != null) {
            return IterableUtils.toList(commentRepository.findAllByPostId(pageableRequest, postId));
        }

        return new ArrayList<>();
    }
}
