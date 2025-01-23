package com.jss.osiris.modules.myjss.wordpress.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.HtmlTruncateHelper;
import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.myjss.wordpress.model.Author;
import com.jss.osiris.modules.myjss.wordpress.model.Category;
import com.jss.osiris.modules.myjss.wordpress.model.Media;
import com.jss.osiris.modules.myjss.wordpress.model.MyJssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.myjss.wordpress.model.PublishingDepartment;
import com.jss.osiris.modules.myjss.wordpress.model.Serie;
import com.jss.osiris.modules.myjss.wordpress.model.Tag;
import com.jss.osiris.modules.myjss.wordpress.repository.PostRepository;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    MediaService mediaService;

    @Autowired
    AuthorService authorService;

    @Autowired
    MyJssCategoryService myJssCategoryService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    PublishingDepartmentService publishingDepartmentService;

    @Autowired
    PostRepository postRepository;

    @Autowired
    TagService tagService;

    @Autowired
    SerieService serieService;

    @Autowired
    ConstantService constantService;

    @Autowired
    BatchService batchService;

    @Autowired
    HtmlTruncateHelper htmlTruncateHelper;

    @Value("${apache.media.base.url}")
    private String apacheMediaBaseUrl;

    @Value("${wordpress.media.base.url}")
    private String wordpressMediaBaseUrl;

    Category categoryArticle = null;
    Category categoryPodcast = null;
    Category categoryInterview = null;

    private Category getCategoryArticle() throws OsirisException {
        if (categoryArticle == null)
            categoryArticle = this.constantService.getCategoryArticle();
        return categoryArticle;
    }

    private Category getCategoryPodcast() throws OsirisException {
        if (categoryPodcast == null)
            categoryPodcast = this.constantService.getCategoryPodcast();
        return categoryPodcast;
    }

    private Category getCategoryInterview() throws OsirisException {
        if (categoryInterview == null)
            categoryInterview = this.constantService.getCategoryInterview();
        return categoryInterview;
    }

    @Override
    public Post getPost(Integer id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent())
            return post.get();
        return null;
    }

    @Override
    public List<Post> getPostTendency() throws OsirisException {
        List<Integer> idPosts = postRepository.findPostTendency(LocalDate.now().minusDays(7), getCategoryArticle(),
                PageRequest.of(0, 5));
        if (idPosts != null)
            return IterableUtils.toList(postRepository.findAllById(idPosts));
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Post addOrUpdatePostFromWordpress(Post post) throws OsirisException {
        post.setIsCancelled(false);
        if (post.getTitle() != null)
            post.setTitleText(post.getTitle().getRendered());
        if (post.getExcerpt() != null)
            post.setExcerptText(post.getExcerpt().getRendered());
        if (post.getContent() != null)
            post.setContentText(post.getContent().getRendered());

        // Matching podcast tag
        Pattern pattern = Pattern.compile("<audio[^>]*\\ssrc=\"(.*?)\"");
        Matcher matcher = pattern.matcher(post.getContentText());
        if (matcher.find()) {
            String url = matcher.group(1);
            Media podcastMedia = mediaService.getMediaByUrl(url);
            if (podcastMedia != null)
                post.setMediaTimeLength(podcastMedia.getLength());

            post.setPodcastUrl(url.replace(wordpressMediaBaseUrl, apacheMediaBaseUrl));
        }

        // Matching video tag
        pattern = Pattern.compile("<video[^>]*\\ssrc=\"(.*?)\"");
        matcher = pattern.matcher(post.getContentText());
        if (matcher.find()) {
            String url = matcher.group(1);
            Media videoMedia = mediaService.getMediaByUrl(url);
            if (videoMedia != null)
                post.setMediaTimeLength(videoMedia.getLength());

            post.setVideoUrl(url.replace(wordpressMediaBaseUrl, apacheMediaBaseUrl));
        }

        postRepository.save(computePost(post));
        batchService.declareNewBatch(Batch.REINDEX_POST, post.getId());
        return post;
    }

    @Override
    public List<Post> getPosts(int page) throws OsirisException {
        return getPostsByCategory(page, getCategoryArticle());
    }

    @Override
    public List<Post> getPostsByMyJssCategory(int page, MyJssCategory myJssCategory) {
        Order order = new Order(Direction.DESC, "date");
        Sort sort = Sort.by(Arrays.asList(order));
        Pageable pageableRequest = PageRequest.of(page, 20, sort);
        return postRepository.findByMyJssCategoriesAndIsCancelled(myJssCategory, false, pageableRequest);
    }

    @Override
    public List<Post> getPostsByTag(Integer page, Tag tag) {
        Order order = new Order(Direction.DESC, "date");
        Sort sort = Sort.by(Arrays.asList(order));
        Pageable pageableRequest = PageRequest.of(page, 20, sort);
        return postRepository.findByPostTagsAndIsCancelled(tag, false, pageableRequest);
    }

    @Override
    public List<Post> getPostsByAuthor(Integer page, Author author) {
        Order order = new Order(Direction.DESC, "date");
        Sort sort = Sort.by(Arrays.asList(order));
        Pageable pageableRequest = PageRequest.of(page, 20, sort);
        return postRepository.findByFullAuthorAndIsCancelled(author, false, pageableRequest);
    }

    @Override
    public List<Post> getTopPostByDepartment(Integer page, PublishingDepartment department) throws OsirisException {
        Order order = new Order(Direction.DESC, "date");
        Sort sort = Sort.by(Arrays.asList(order));
        Pageable pageableRequest = PageRequest.of(page, 20, sort);
        return postRepository.findByPostCategoriesAndIsCancelledAndDepartments(getCategoryArticle(), false, department,
                pageableRequest);
    }

    private List<Post> getPostsByCategory(int page, Category category) {
        Order order = new Order(Direction.DESC, "date");
        Sort sort = Sort.by(Arrays.asList(order));
        Pageable pageableRequest = PageRequest.of(page, 20, sort);
        return postRepository.findByPostCategoriesAndIsCancelled(category, false, pageableRequest);
    }

    @Override
    public List<Post> getPostInterview(int page) throws OsirisException {
        return getPostsByCategory(page, getCategoryInterview());
    }

    @Override
    public List<Post> getPostPodcast(int page) throws OsirisException {
        return getPostsByCategory(page, getCategoryPodcast());
    }

    @Override
    public Post getPostsBySlug(String slug) {
        return postRepository.findBySlugAndIsCancelled(slug, false);
    }

    @Override
    public List<Post> getPostBySerie(Serie serie) {
        return postRepository.findByPostSerieAndIsCancelled(serie, false);
    }

    @Override
    public Post getNextPost(Post post) {
        if (post != null && post.getMyJssCategories() != null && post.getMyJssCategories().size() > 0) {
            Order order = new Order(Direction.ASC, "date");
            Sort sort = Sort.by(Arrays.asList(order));
            Pageable pageableRequest = PageRequest.of(0, 1, sort);
            List<Post> posts = postRepository.findNextArticle(post.getMyJssCategories().get(0), post.getDate(),
                    pageableRequest);
            if (posts != null && posts.size() > 0)
                return posts.get(0);
        }
        return null;
    }

    @Override
    public Post getPreviousPost(Post post) {
        if (post != null && post.getMyJssCategories() != null && post.getMyJssCategories().size() > 0) {
            Order order = new Order(Direction.DESC, "date");
            Sort sort = Sort.by(Arrays.asList(order));
            Pageable pageableRequest = PageRequest.of(0, 1, sort);
            List<Post> posts = postRepository.findPreviousArticle(post.getMyJssCategories().get(0), post.getDate(),
                    pageableRequest);
            if (posts != null && posts.size() > 0)
                return posts.get(0);
        }
        return null;
    }

    @Override
    public List<Post> applyPremium(List<Post> posts) {
        if (posts != null)
            for (Post post : posts) {
                applyPremium(post);
            }
        return posts;
    }

    @Override
    public Post applyPremium(Post post) {
        if (post != null)
            if (post.getIsPremium() != null && post.getIsPremium()) {
                // TODO : check user connected and have the right
                boolean haveTheRight = false;
                if (!haveTheRight) {
                    Integer percentage = 20;
                    if (post.getPremiumPercentage() != null && post.getPremiumPercentage() > 0)
                        percentage = post.getPremiumPercentage();

                    post.setContentText(htmlTruncateHelper.truncateHtml(post.getContentText(), percentage));
                }
            }
        return post;
    }

    private Post computePost(Post post) {
        if (post.getFeatured_media() != null && post.getFeatured_media() > 0) {
            post.setMedia(mediaService.getMedia(post.getFeatured_media()));
        }
        if (post.getAcf() != null) {
            post.setIsPremium(post.getAcf().isPremium());
            post.setPremiumPercentage(post.getAcf().getPremium_percentage());
            if (post.getAcf().getAssociated_post() != null) {
                List<Post> postList = new ArrayList<Post>();
                for (Integer postId : post.getAcf().getAssociated_post()) {
                    postList.add(getPost(postId));
                }
                post.setRelatedPosts(postList);
            }
        }
        if (post.getAuthor() != null && post.getAuthor() > 0)
            post.setFullAuthor(authorService.getAuthor(post.getAuthor()));
        if (post.getMyjss_category() != null && post.getMyjss_category().length > 0) {
            List<MyJssCategory> categories = new ArrayList<MyJssCategory>();
            List<MyJssCategory> availableCategories = myJssCategoryService.getAvailableMyJssCategories();
            for (Integer i : post.getMyjss_category()) {
                for (MyJssCategory availableCategory : availableCategories) {
                    if (availableCategory.getId().equals(i)) {
                        categories.add(availableCategory);
                        break;
                    }
                }
            }
            post.setMyJssCategories(categories);
        }
        if (post.getCategories() != null && post.getCategories().length > 0) {
            List<Category> categories = new ArrayList<Category>();
            List<Category> availableCategories = categoryService.getAvailableCategories();
            for (Integer i : post.getCategories()) {
                for (Category availableCategory : availableCategories) {
                    if (availableCategory.getId().equals(i)) {
                        categories.add(availableCategory);
                        break;
                    }
                }
            }
            post.setPostCategories(categories);
        }
        if (post.getDepartement() != null && post.getDepartement().length > 0) {
            List<PublishingDepartment> departments = new ArrayList<PublishingDepartment>();
            List<PublishingDepartment> availableDepartments = publishingDepartmentService.getAvailableDepartments();
            for (Integer i : post.getDepartement()) {
                for (PublishingDepartment department : availableDepartments) {
                    if (department.getId().equals(i)) {
                        departments.add(department);
                        break;
                    }
                }
            }
            post.setDepartments(departments);
        }
        if (post.getTags() != null && post.getTags().length > 0) {
            List<Tag> tags = new ArrayList<Tag>();
            List<Tag> availableTags = tagService.getAvailableTags();
            for (Integer i : post.getTags()) {
                for (Tag tag : availableTags) {
                    if (tag.getId().equals(i)) {
                        tags.add(tag);
                        break;
                    }
                }
            }
            post.setPostTags(tags);
        }

        if (post.getSerie() != null && post.getSerie().length > 0) {
            List<Serie> series = new ArrayList<Serie>();
            List<Serie> availableSeries = serieService.getAvailableSeries();
            for (Integer i : post.getSerie()) {
                for (Serie serie : availableSeries) {
                    if (serie.getId().equals(i)) {
                        series.add(serie);
                        break;
                    }
                }
            }
            post.setPostSerie(series);
        }
        return post;
    }

    public List<Post> getPostExcludedId(List<Integer> postFetchedId) {
        return postRepository.findPostExcludIds(postFetchedId);
    }

    public void cancelPost(Post post) {
        post = getPost(post.getId());
        post.setIsCancelled(true);
        postRepository.save(post);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reindexPosts() throws OsirisException {
        List<Post> posts = IterableUtils.toList(postRepository.findAll());
        if (posts != null)
            for (Post post : posts)
                batchService.declareNewBatch(Batch.REINDEX_POST, post.getId());
    }
}
