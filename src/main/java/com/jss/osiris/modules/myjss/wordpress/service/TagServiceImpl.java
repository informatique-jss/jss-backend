package com.jss.osiris.modules.myjss.wordpress.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.myjss.wordpress.model.AssoMailTag;
import com.jss.osiris.modules.myjss.wordpress.model.Author;
import com.jss.osiris.modules.myjss.wordpress.model.Category;
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

    @Autowired
    AssoMailTagService assoMailTagService;

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
        if (jssCategory != null) {
            Pageable pageable = PageRequest.of(0, 1000000,
                    Sort.by(Sort.Direction.DESC, "date"));
            Page<Post> posts = postService.getAllPostsByJssCategory(pageable, jssCategory, null,
                    LocalDateTime.of(1970, 1, 1, 0, 0));
            return computeTagsLinkedToPost(posts);
        }
        return null;
    }

    @Override
    public Page<Tag> getAllTagsByCategory(Pageable pageable, Category category) {
        List<Tag> tags = new ArrayList<>();
        if (category != null) {
            Page<Post> posts = postService.getAllPostsByCategory(pageable, category);

            for (Post post : posts.getContent()) {
                if (post.getPostTags() != null && !post.getPostTags().isEmpty())
                    tags.addAll(post.getPostTags());
            }
        }
        return new PageImpl<>(tags);
    }

    @Override
    public List<Tag> getAllTagsByTag(Tag tag, LocalDateTime consultationDate) throws OsirisException {
        if (tag != null) {
            Pageable pageable = PageRequest.of(0, 1000000,
                    Sort.by(Sort.Direction.DESC, "date"));
            Page<Post> posts = postService.getAllPostsByTag(pageable, tag, null, consultationDate);

            return computeTagsLinkedToPost(posts);
        }
        return null;
    }

    @Override
    public List<Tag> getAllTagsByAuthor(Author author, LocalDateTime consuLocalDateTime) {
        if (author != null) {
            Pageable pageable = PageRequest.of(0, 1000000,
                    Sort.by(Sort.Direction.DESC, "date"));
            Page<Post> posts = postService.getAllPostsByAuthor(pageable, author, null, consuLocalDateTime);

            return computeTagsLinkedToPost(posts);
        }
        return null;
    }

    private List<Tag> computeTagsLinkedToPost(Page<Post> posts) {
        List<Tag> tags = new ArrayList<>();
        List<Integer> tagIds = new ArrayList<>();

        if (posts != null && !posts.getContent().isEmpty())
            for (Post post : posts.getContent()) {
                if (post.getPostTags() != null && !post.getPostTags().isEmpty())
                    for (Tag tag : post.getPostTags())
                        if (!tagIds.contains(tag.getId())) {
                            tags.add(tag);
                            tagIds.add(tag.getId());
                        }
            }
        return tags;
    }

    @Override
    public List<Tag> getAllTagsBySerie(Serie serie) {
        if (serie != null) {
            Pageable pageable = PageRequest.of(0, 1000000,
                    Sort.by(Sort.Direction.DESC, "date"));
            Page<Post> posts = postService.getAllPostsBySerie(pageable, serie, null);

            return computeTagsLinkedToPost(posts);
        }
        return null;
    }

    @Override
    public List<Tag> getAllTagsByPublishingDepartment(PublishingDepartment publishingDepartment)
            throws OsirisException {
        if (publishingDepartment != null) {
            Pageable pageable = PageRequest.of(0, 1000000,
                    Sort.by(Sort.Direction.DESC, "date"));
            Page<Post> posts = postService.getAllPostsByPublishingDepartment(pageable, publishingDepartment, null);
            return computeTagsLinkedToPost(posts);
        }
        return null;
    }

    @Override
    public List<Tag> getAllTagsByIdf() throws OsirisException {
        Pageable pageable = PageRequest.of(0, 1000000,
                Sort.by(Sort.Direction.DESC, "date"));
        Page<Post> posts = postService.getAllPostsByIdf(pageable, null);

        return computeTagsLinkedToPost(posts);
    }

    @Override
    public List<Tag> getAllTendencyTags() throws OsirisException {
        List<Tag> tags = new ArrayList<Tag>();
        List<Integer> tagsAdded = new ArrayList<Integer>();

        Pageable pageable = PageRequest.of(0, ValidationHelper.limitPageSize(15),
                Sort.by(Sort.Direction.DESC, "date"));

        List<Post> posts = postService.getJssCategoryPostTendency(null, pageable).getContent();

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

    @Override
    public List<Tag> getFollowedTagsForCurrentUser() {
        List<Tag> tags = new ArrayList<>();
        List<AssoMailTag> assoMailTags = assoMailTagService.getAssoMailTagForCurrentUser();

        if (!assoMailTags.isEmpty()) {
            for (AssoMailTag asso : assoMailTags)
                tags.add(asso.getTag());
        }
        return tags;
    }
}
