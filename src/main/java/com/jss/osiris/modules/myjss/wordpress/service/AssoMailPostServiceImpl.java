package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.myjss.wordpress.model.AssoMailPost;
import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.myjss.wordpress.repository.AssoMailPostRepository;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;

@Service
public class AssoMailPostServiceImpl implements AssoMailPostService {

    @Autowired
    AssoMailPostRepository assoMailPostRepository;

    @Override
    public AssoMailPost addOrUpdateAssoMailPost(AssoMailPost assoMailPost) {
        return assoMailPostRepository.save(assoMailPost);
    }

    @Override
    public AssoMailPost getAssoMailPostByMailAndPost(Mail mail, Post post) {
        return assoMailPostRepository.findByMailAndPost(mail, post);
    }

    @Override
    public List<AssoMailPost> getAssoMailPostsByMail(Mail mail) {
        return assoMailPostRepository.findByMail(mail);
    }

    @Override
    public void deleteAssoMailPost(AssoMailPost assoMailPost) {
        assoMailPostRepository.delete(assoMailPost);
    }
}
