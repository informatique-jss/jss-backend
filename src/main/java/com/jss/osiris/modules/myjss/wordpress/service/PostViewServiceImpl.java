package com.jss.osiris.modules.myjss.wordpress.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.myjss.wordpress.model.PostView;
import com.jss.osiris.modules.myjss.wordpress.repository.PostViewRepository;

@Service
public class PostViewServiceImpl implements PostViewService {

    @Autowired
    PostViewRepository postViewRepository;

    @Autowired
    PostService postService;

    public PostView addOrUpdatePostView(PostView postView) {
        return postViewRepository.save(postView);
    }

    @Transactional(rollbackFor = Exception.class)
    public void incrementView(Post post) {
        postViewRepository.incrementPostViewDay(post.getId(),
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }
}
