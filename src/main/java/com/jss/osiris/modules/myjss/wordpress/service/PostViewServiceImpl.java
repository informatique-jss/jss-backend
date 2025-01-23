package com.jss.osiris.modules.myjss.wordpress.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.myjss.wordpress.model.PostView;
import com.jss.osiris.modules.myjss.wordpress.repository.PostViewRepository;

@Service
public class PostViewServiceImpl implements PostViewService {

    @Autowired
    PostViewRepository postViewRepository;

    @Autowired
    PostService postService;

    private PostView addOrUpdatePostView(PostView postView) {
        return postViewRepository.save(postView);
    }

    public void incrementView(Post post) {
        post = postService.getPost(post.getId());
        if (post != null) {
            PostView postView = postViewRepository.findByPostAndDay(post, LocalDate.now());
            if (postView == null) {
                postView = new PostView();
                postView.setPost(post);
                postView.setDay(LocalDate.now());
                postView.setCount(0);
            }
            postView.setCount(postView.getCount() + 1);
            addOrUpdatePostView(postView);
        }
    }
}
