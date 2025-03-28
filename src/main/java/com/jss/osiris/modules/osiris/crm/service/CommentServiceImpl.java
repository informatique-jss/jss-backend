package com.jss.osiris.modules.osiris.crm.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.myjss.wordpress.service.PostService;
import com.jss.osiris.modules.osiris.crm.model.Comment;
import com.jss.osiris.modules.osiris.crm.model.CommentSearch;
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
    public Page<Comment> getComments(CommentSearch commentSearch, Pageable pageableRequest) {

        if (commentSearch.getCreationDate() == null)
            commentSearch.setCreationDate(LocalDateTime.of(1970, 01, 01, 0, 0, 0));

        Integer postId = null;
        if (commentSearch.getPost() != null)
            postId = commentSearch.getPost().getEntityId();

        if (commentSearch.getContent() == null)
            commentSearch.setContent("");

        if (commentSearch.getAuthorFirstLastName() == null)
            commentSearch.setAuthorFirstLastName("");

        Page<Comment> comments = commentRepository.findFiltered(
                commentSearch.getCreationDate(),
                postId,
                commentSearch.getContent().toLowerCase(),
                commentSearch.getAuthorFirstLastName().toLowerCase(),
                commentSearch.getIsModerated(),
                commentSearch.getIsDeleted(),
                pageableRequest);

        return comments;
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
    public Comment addOrUpdateComment(Comment comment, Integer parentCommentId, Integer postId) throws OsirisException {

        // Make sure the mail exists or is created before beeing linked with the comment
        mailService.populateMailId(comment.getMail());

        Post post = postService.getPost(postId);

        if (post != null) {
            comment.setPost(post);

            if (parentCommentId != null)
                comment.setParentComment(this.getComment(parentCommentId));

            if (comment.getCreationDate() == null)
                comment.setCreationDate(LocalDateTime.now());

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
    public Page<Comment> getParentCommentsForPost(Pageable pageableRequest, Integer postId) {
        Post post = postService.getPost(postId);

        if (post != null) {
            Page<Comment> comments = commentRepository.findAllByPostIdAndIsDeletedFalseAndParentCommentIsNull(postId,
                    pageableRequest);
            return comments;
        }

        return Page.empty();
    }
}