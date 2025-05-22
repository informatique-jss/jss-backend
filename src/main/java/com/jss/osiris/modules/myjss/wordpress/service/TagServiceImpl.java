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

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.myjss.wordpress.model.Author;
import com.jss.osiris.modules.myjss.wordpress.model.JssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.myjss.wordpress.model.PublishingDepartment;
import com.jss.osiris.modules.myjss.wordpress.model.Serie;
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

    @Override
    public List<Tag> getAllTagsByTag(Tag tag) throws OsirisException {
        List<Tag> tags = new ArrayList<>();
        if (tag != null) {
            Pageable pageable = PageRequest.of(0, 1000000,
                    Sort.by(Sort.Direction.DESC, "date"));
            Page<Post> posts = postService.getAllPostsByTag(pageable, tag, null);

            for (Post post : posts.getContent()) {
                if (post.getPostTags() != null && !post.getPostTags().isEmpty())
                    tags.addAll(post.getPostTags());
            }
        }
        return tags;
    }

    @Override
    public List<Tag> getAllTagsByAuthor(Author author) {
        List<Tag> tags = new ArrayList<>();
        if (author != null) {
            Pageable pageable = PageRequest.of(0, 1000000,
                    Sort.by(Sort.Direction.DESC, "date"));
            Page<Post> posts = postService.getAllPostsByAuthor(pageable, author, null);

            for (Post post : posts.getContent()) {
                if (post.getPostTags() != null && !post.getPostTags().isEmpty())
                    tags.addAll(post.getPostTags());
            }
        }
        return tags;
    }

    @Override
    public List<Tag> getAllTagsBySerie(Serie serie) {
        List<Tag> tags = new ArrayList<>();
        if (serie != null) {
            Pageable pageable = PageRequest.of(0, 1000000,
                    Sort.by(Sort.Direction.DESC, "date"));
            Page<Post> posts = postService.getAllPostsBySerie(pageable, serie, null);

            for (Post post : posts.getContent()) {
                if (post.getPostTags() != null && !post.getPostTags().isEmpty())
                    tags.addAll(post.getPostTags());
            }
        }
        return tags;
    }

    @Override
    public List<Tag> getAllTagsByPublishingDepartment(PublishingDepartment publishingDepartment)
            throws OsirisException {
        List<Tag> tags = new ArrayList<>();
        if (publishingDepartment != null) {
            Pageable pageable = PageRequest.of(0, 1000000,
                    Sort.by(Sort.Direction.DESC, "date"));
            Page<Post> posts = postService.getAllPostsByPublishingDepartment(pageable, publishingDepartment, null);

            for (Post post : posts.getContent()) {
                if (post.getPostTags() != null && !post.getPostTags().isEmpty())
                    tags.addAll(post.getPostTags());
            }
        }
        return tags;
    }

    @Override
    public List<Tag> getAllTagsByIdf() throws OsirisException {
        List<Tag> tags = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, 1000000,
                Sort.by(Sort.Direction.DESC, "date"));
        Page<Post> posts = postService.getAllPostsByIdf(pageable, null);

        for (Post post : posts.getContent()) {
            if (post.getPostTags() != null && !post.getPostTags().isEmpty())
                tags.addAll(post.getPostTags());
        }
        return tags;
    }

    @Override
    public List<Tag> getAllTendencyTags() throws OsirisException {
        List<Tag> tags = new ArrayList<Tag>();
        List<Integer> tagsAdded = new ArrayList<Integer>();
        List<Post> posts = postService.getJssCategoryPostTendency();
        if (posts != null)
            for (Post post : posts)
                if (post.getPostTags() != null) {
                    for (Tag tag : post.getPostTags()) {
                        if (!tagsAdded.contains(tag.getId())) {
                            tags.add(tag);
                            tagsAdded.add(tag.getId());
                        }
                    }
                }
        return tags;
    }
}
