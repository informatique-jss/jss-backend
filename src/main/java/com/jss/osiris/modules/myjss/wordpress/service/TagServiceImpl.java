package com.jss.osiris.modules.myjss.wordpress.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.myjss.wordpress.model.JssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.myjss.wordpress.model.Tag;
import com.jss.osiris.modules.myjss.wordpress.repository.TagRepository;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    TagRepository tagRepository;

    @Autowired
    PostService postService;

    @Override
    public Tag getTag(Integer id) {
        Optional<Tag> tag = tagRepository.findById(id);
        if (tag.isPresent())
            return tag.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Tag addOrUpdateTagFromWordpress(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public List<Tag> getAvailableTags() {
        return IterableUtils.toList(tagRepository.findAll());
    }

    @Override
    public Tag getTagBySlug(String slug) {
        return tagRepository.findBySlug(slug);
    }

    @Override
    public List<Tag> getAllTagsByJssCategory(JssCategory jssCategory) {
        List<Tag> tags = new ArrayList<>();
        if (jssCategory != null) {
            Pageable pageable = PageRequest.of(0, 1000000,
                    Sort.by(Sort.Direction.DESC, "date"));
            Page<Post> posts = postService.getAllPostsByJssCategory(pageable, jssCategory, null);

            for (Post post : posts.getContent()) {
                if (post.getPostTags() != null && !post.getPostTags().isEmpty())
                    tags.addAll(post.getPostTags());
            }
        }
        return tags;
    }
}
