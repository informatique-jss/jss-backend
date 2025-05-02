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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import com.jss.osiris.libs.search.model.IndexEntity;
import com.jss.osiris.libs.search.service.SearchService;
import com.jss.osiris.modules.myjss.wordpress.model.Author;
import com.jss.osiris.modules.myjss.wordpress.model.Category;
import com.jss.osiris.modules.myjss.wordpress.model.JssCategory;
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
    JssCategoryService jssCategoryService;

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
    SearchService searchService;

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
    public List<Post> getJssCategoryPostTendency() throws OsirisException {
        List<Integer> idPosts = postRepository.findJssCategoryPostTendency(LocalDate.now().minusDays(7),
                getCategoryArticle(),
                PageRequest.of(0, 5));
        if (idPosts != null)
            return IterableUtils.toList(postRepository.findAllById(idPosts));
        return null;
    }

    @Override
    public List<Post> getMyJssCategoryPostTendency() throws OsirisException {
        List<Integer> idPosts = postRepository.findMyJssCategoryPostTendency(LocalDate.now().minusDays(7),
                PageRequest.of(0, 5));
        if (idPosts != null)
            return IterableUtils.toList(postRepository.findAllById(idPosts));
        return null;
    }

    @Override
    public List<Post> getMyJssCategoryPostMostSeen() throws OsirisException {
        List<Integer> idPosts = postRepository.findMyJssCategoryPostMostSeen(PageRequest.of(0, 5));
        if (idPosts != null)
            return IterableUtils.toList(postRepository.findAllById(idPosts));
        return null;
    }

    @Override
    public Page<Post> getMostSeenPostByJssCatgory(Pageable pageableRequest, JssCategory jssCategory) {
        return postRepository.findMostSeenPostJssCategory(pageableRequest, jssCategory);
    }

    @Override
    public Page<Post> getMostSeenPostByTag(Pageable pageableRequest, Tag tag) {
        return postRepository.findMostSeenPostTag(pageableRequest, tag);
    }

    @Override
    public Page<Post> getMostSeenPostByAuthor(Pageable pageableRequest, Author author) {
        return postRepository.findMostSeenPostAuthor(pageableRequest, author);
    }

    @Override
    public Page<Post> getMostSeenPostBySerie(Pageable pageableRequest, Serie serie) {
        return postRepository.findMostSeenPostSerie(pageableRequest, serie);
    }

    @Override
    public Page<Post> getMostSeenPostByPublishingDepartment(Pageable pageableRequest,
            PublishingDepartment publishingDepartment) {
        return postRepository.findMostSeenPostPublishingDepartment(pageableRequest, publishingDepartment);
    }

    @Override
    public Page<Post> getMostSeenPostByIdf(Pageable pageableRequest) {
        return postRepository.findPostsIdf(pageableRequest);
    }

    @Override
    public Page<Post> getJssCategoryPostMostSeen(Pageable pageableRequest) throws OsirisException {
        return postRepository.findJssCategoryPostMostSeen(pageableRequest);
    }

    @Override
    public Page<Post> getJssCategoryStickyPost(Pageable pageableRequest) throws OsirisException {
        return postRepository.findJssCategoryPostMostSeen(pageableRequest);
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

        modifyPodcastUrls(post);
        modifyVideoUrls(post);
        modifyHrefToOpenInNewTab(post);
        reformatQuotes(post);
        reformatFootnotes(post);

        postRepository.save(computePost(post));
        batchService.declareNewBatch(Batch.REINDEX_POST, post.getId());
        return post;
    }

    /**
     * Matching podcast tag
     * 
     * @param post
     */
    private void modifyPodcastUrls(Post post) {
        Pattern pattern = Pattern.compile("<audio[^>]*\\ssrc=\"(.*?)\"");
        Matcher matcher = pattern.matcher(post.getContentText());
        if (matcher.find()) {
            String url = matcher.group(1);
            Media podcastMedia = mediaService.getMediaByUrl(url);
            if (podcastMedia != null)
                post.setMediaTimeLength(podcastMedia.getLength());

            post.setPodcastUrl(url.replace(wordpressMediaBaseUrl, apacheMediaBaseUrl));
        }
    }

    /**
     * Matching video tag
     * 
     * @param post
     */
    private void modifyVideoUrls(Post post) {
        Pattern pattern = Pattern.compile("<video[^>]*\\ssrc=\"(.*?)\"");
        Matcher matcher = pattern.matcher(post.getContentText());
        if (matcher.find()) {
            String url = matcher.group(1);
            Media videoMedia = mediaService.getMediaByUrl(url);
            if (videoMedia != null)
                post.setMediaTimeLength(videoMedia.getLength());

            post.setVideoUrl(url.replace(wordpressMediaBaseUrl, apacheMediaBaseUrl));
        }
    }

    /**
     * Changing HTML to open external href in a new tab
     * 
     * @param post
     */
    private void modifyHrefToOpenInNewTab(Post post) {
        Pattern pattern;
        Matcher matcher;
        final String TARGET_BLANK = " target=\"_blank\"";
        pattern = Pattern.compile("<a\\s+[^>]*href=\"([^\"]+)\".*?>(.*?)<\\/a>");

        matcher = pattern.matcher(post.getContentText());

        int insertions = 0; // Keeps track of the number of insertions of the ATTRIBUTE_TO_INSERT attribute
        while (matcher.find()) {
            String hrefElement = matcher.group();
            if (!hrefElement.contains(TARGET_BLANK)) {
                // We want to insert after the " of the found url (+1) and shift the end index
                // everytime we add the attribute TARGET_BLANK
                int indexToInsert = matcher.end(1) + 1 + insertions * TARGET_BLANK.length();
                String newString = post.getContentText().substring(0, indexToInsert)
                        + TARGET_BLANK
                        + post.getContentText().substring(indexToInsert);
                post.setContentText(newString);
                insertions++;
            }
        }
    }

    /**
     * Changing HTML for formatting quotes
     * 
     * @param post
     */
    private void reformatQuotes(Post post) {
        Pattern pattern = Pattern.compile("(?s)<blockquote[^>]*\\sclass=\"([^\"]+)\".*?</blockquote>");
        Matcher matcher = pattern.matcher(post.getContentText());

        while (matcher.find()) {
            String blockquoteElement = matcher.group();

            // Addind a <figure> tag encapsulating the quote
            post.setContentText(post.getContentText().replaceFirst(escapeRegexSpecialChars(blockquoteElement),
                    "<figure class=\"my-5\">" + blockquoteElement + "</figure>"));

            // Replacing the <blockquote> wordpress classes by the "blockquote" class
            String wordpressQuoteClasses = matcher.group(1);
            post.setContentText(
                    post.getContentText().replaceFirst(escapeRegexSpecialChars(wordpressQuoteClasses), "blockquote"));

        }

        // We modify all the <cite> tag classes with good format
        final String CITE_TAG = "<cite";
        post.setContentText(post.getContentText().replace(CITE_TAG,
                CITE_TAG + " class=\"blockquote-footer\" style=\"padding-left:0\""));
    }

    /**
     * The method allow to escape all characters that a regex could interpret as
     * REGEX char so the only text that is interpreted by the REGEX matcher is the
     * plain text that we are searching
     * 
     * @param textToFind
     * @return
     */
    private static String escapeRegexSpecialChars(String textToFind) {
        return textToFind.replaceAll("([\\^\\$\\.\\|\\?\\*\\+\\(\\)\\[\\]\\{\\}\\\\])", "\\\\$1");
    }

    /**
     * Reformating footnotes so that the anchor is properly done in the site
     * 
     * @param post
     */
    private void reformatFootnotes(Post post) {
        Pattern pattern = Pattern.compile(
                "<sup\\s+data-fn=\"([0-9a-fA-F\\-]+)\"\\s+class=\"fn\"><a[^>]*id=\"\\1-link\"[^>]*>(.*?)<\\/a><\\/sup>");
        Matcher matcher = pattern.matcher(post.getContentText());

        while (matcher.find()) {
            String id = matcher.group(1);
            String footnoteNumber = matcher.group(2);
            String supElement = matcher.group();
            String newSupTag = "<sup onclick=\"scrollToElement('" + id + "')\" style=\"cursor: pointer;\" id=\"" + id
                    + "-link\">" + footnoteNumber + "</sup>";

            // Changing the <sup> tag of the footnote
            post.setContentText(post.getContentText().replace(supElement, newSupTag));

            // Changing the footnote it self to have it link to the <sup> tag
            post.setContentText(post.getContentText().replace("<a href=\"#" + id + "-link\" target=\"_blank\"",
                    "<a onclick=\"scrollToElement('" + id + "-link')\" style=\"cursor: pointer;\""));
        }
    }

    @Override
    public Page<Post> getJssCategoryPosts(Pageable pageableRequest) throws OsirisException {

        return postRepository.findJssCategoryPosts(getCategoryArticle(), false,
                pageableRequest);
    }

    @Override
    public List<Post> getMyJssCategoryPosts(int page) throws OsirisException {
        Order order = new Order(Direction.DESC, "date");
        Sort sort = Sort.by(Arrays.asList(order));
        Pageable pageableRequest = PageRequest.of(page, 20, sort);
        return postRepository.findMyJssCategoryPosts(false, pageableRequest);
    }

    @Override
    public Page<Post> getAllPostsByJssCategory(Pageable pageableRequest, JssCategory jssCategory, String searchText) {
        if (searchText != null) {
            List<IndexEntity> tmpEntitiesFound = null;
            tmpEntitiesFound = searchService.searchForEntities(searchText, Post.class.getSimpleName(), false);
            if (tmpEntitiesFound != null && tmpEntitiesFound.size() > 0) {
                return searchPostAgainstEntitiesToMatch(searchText,
                        postRepository.findByJssCategoriesAndIsCancelled(jssCategory, false,
                                pageableRequest));
            }
        }
        return postRepository.findByJssCategoriesAndIsCancelled(jssCategory, false, pageableRequest);
    }

    private Page<Post> searchPostAgainstEntitiesToMatch(String searchText, Page<Post> entityToMatchWithResearch) {
        List<IndexEntity> tmpEntitiesFound = null;
        List<Post> matchingPosts = new ArrayList<Post>();

        tmpEntitiesFound = searchService.searchForEntities(searchText, Post.class.getSimpleName(), false);
        if (tmpEntitiesFound != null && tmpEntitiesFound.size() > 0) {
            if (entityToMatchWithResearch != null) {
                for (Post post : entityToMatchWithResearch) {
                    for (IndexEntity entity : tmpEntitiesFound) {
                        if (post.getId().equals(entity.getEntityId()))
                            matchingPosts.add(post);
                    }
                }
                PageRequest newPageRequest = PageRequest.of(0, entityToMatchWithResearch.getSize());
                Page<Post> pageResult = new PageImpl<>(matchingPosts, newPageRequest, matchingPosts.size());
                return pageResult;
            }
        }
        return null;
    }

    @Override
    public Page<Post> getAllPostsByIdf(Pageable pageableRequest, String searchText) {
        if (searchText != null) {
            List<IndexEntity> tmpEntitiesFound = null;
            tmpEntitiesFound = searchService.searchForEntities(searchText, Post.class.getSimpleName(), false);
            if (tmpEntitiesFound != null && tmpEntitiesFound.size() > 0) {
                return searchPostAgainstEntitiesToMatch(searchText, postRepository.findPostsIdf(pageableRequest));
            }
        }
        return postRepository.findPostsIdf(pageableRequest);
    }

    @Override
    public Page<Post> getPostsByJssCategory(Pageable pageableRequest, JssCategory jssCategory) {
        return postRepository.findByJssCategoriesAndIsCancelled(jssCategory, false, pageableRequest);
    }

    @Override
    public Page<Post> getAllPostsByTag(Pageable pageableRequest, Tag tag, String searchText) {
        if (searchText != null) {
            List<IndexEntity> tmpEntitiesFound = null;
            tmpEntitiesFound = searchService.searchForEntities(searchText, Post.class.getSimpleName(), false);
            if (tmpEntitiesFound != null && tmpEntitiesFound.size() > 0) {
                return searchPostAgainstEntitiesToMatch(searchText,
                        postRepository.findByPostTagsAndIsCancelled(tag, false,
                                pageableRequest));
            }
        }
        return postRepository.findByPostTagsAndIsCancelled(tag, false, pageableRequest);
    }

    @Override
    public Page<Post> getAllPostsByAuthor(Pageable pageableRequest, Author author, String searchText) {
        if (searchText != null) {
            List<IndexEntity> tmpEntitiesFound = null;
            tmpEntitiesFound = searchService.searchForEntities(searchText, Post.class.getSimpleName(), false);
            if (tmpEntitiesFound != null && tmpEntitiesFound.size() > 0) {
                return searchPostAgainstEntitiesToMatch(searchText,
                        postRepository.findByFullAuthorAndIsCancelled(author, false,
                                pageableRequest));
            }
        }
        return postRepository.findByFullAuthorAndIsCancelled(author, false, pageableRequest);
    }

    @Override
    public Page<Post> getAllPostsBySerie(Pageable pageableRequest, Serie serie, String searchText) {
        if (searchText != null) {
            List<IndexEntity> tmpEntitiesFound = null;
            tmpEntitiesFound = searchService.searchForEntities(searchText, Post.class.getSimpleName(), false);
            if (tmpEntitiesFound != null && tmpEntitiesFound.size() > 0) {
                return searchPostAgainstEntitiesToMatch(searchText,
                        postRepository.findByPostSerieAndIsCancelled(serie, false,
                                pageableRequest));
            }
        }
        return postRepository.findByPostSerieAndIsCancelled(serie, false, pageableRequest);
    }

    @Override
    public Page<Post> getAllPostsByPublishingDepartment(Pageable pageableRequest,
            PublishingDepartment publishingDepartment, String searchText) {

        if (searchText != null) {
            List<IndexEntity> tmpEntitiesFound = null;
            tmpEntitiesFound = searchService.searchForEntities(searchText, Post.class.getSimpleName(), false);
            if (tmpEntitiesFound != null && tmpEntitiesFound.size() > 0) {
                return searchPostAgainstEntitiesToMatch(searchText,
                        postRepository.findByDepartmentsAndIsCancelled(publishingDepartment,
                                false,
                                pageableRequest));
            }
        }
        return postRepository.findByDepartmentsAndIsCancelled(publishingDepartment, false, pageableRequest);
    }

    @Override
    public Page<Post> getPostsByMyJssCategory(int page, MyJssCategory myJssCategory) {
        Order order = new Order(Direction.DESC, "date");
        Sort sort = Sort.by(Arrays.asList(order));
        Pageable pageableRequest = PageRequest.of(page, 20, sort);
        return postRepository.findByMyJssCategoriesAndIsCancelled(myJssCategory, false, pageableRequest);
    }

    @Override
    public Page<Post> searchPostsByMyJssCategory(String searchText, MyJssCategory myJssCategory,
            Pageable pageableRequest) {
        if (searchText != null && searchText.trim().length() > 0) {
            List<IndexEntity> tmpEntitiesFound = null;
            tmpEntitiesFound = searchService.searchForEntities(searchText, Post.class.getSimpleName(), false);
            if (tmpEntitiesFound != null && tmpEntitiesFound.size() > 0) {
                return searchPostAgainstEntitiesToMatch(searchText,
                        postRepository.findByMyJssCategoriesAndIsCancelled(myJssCategory, false,
                                pageableRequest));
            }
        }
        return postRepository.findByMyJssCategoriesAndIsCancelled(myJssCategory, false,
                pageableRequest);
    }

    @Override
    public Page<Post> searchPostsByCategory(String searchText, Category category,
            Pageable pageableRequest) {
        if (searchText != null && searchText.trim().length() > 0) {
            List<IndexEntity> tmpEntitiesFound = null;
            tmpEntitiesFound = searchService.searchForEntities(searchText, Post.class.getSimpleName(), false);
            if (tmpEntitiesFound != null && tmpEntitiesFound.size() > 0) {
                return searchPostAgainstEntitiesToMatch(searchText,
                        postRepository.findByPostCategoriesAndIsCancelled(category, false, pageableRequest));
            }
        }
        return postRepository.findByPostCategoriesAndIsCancelled(category, false, pageableRequest);
    }

    @Override
    public List<Post> getFirstPostsByMyJssCategories(MyJssCategory selectedMyJssCategory) {
        List<Post> firstPostsByMyJssCategory = new ArrayList<Post>();
        List<Integer> idPostsByMyJssCategory = new ArrayList<Integer>();
        List<Post> tmpPostsByMyJssCategory = new ArrayList<Post>();

        Order order = new Order(Direction.DESC, "date");
        Sort sort = Sort.by(Arrays.asList(order));
        Pageable pageableRequest = PageRequest.of(0, 3, sort);

        if (selectedMyJssCategory != null)
            return postRepository.searchPostsByMyJssCategory(selectedMyJssCategory, pageableRequest);

        for (MyJssCategory myJssCategory : myJssCategoryService.getAvailableMyJssCategories()) {
            tmpPostsByMyJssCategory = postRepository.searchPostsByMyJssCategory(myJssCategory, pageableRequest);
            if (!tmpPostsByMyJssCategory.isEmpty()) {
                for (Post post : tmpPostsByMyJssCategory) {
                    if (!idPostsByMyJssCategory.contains(post.getId())) {
                        idPostsByMyJssCategory.add(post.getId());
                        firstPostsByMyJssCategory.add(post);
                    }
                }
            }
        }
        return firstPostsByMyJssCategory;
    }

    @Override
    public Page<Post> getPostsByTag(Integer page, Tag tag) {
        Order order = new Order(Direction.DESC, "date");
        Sort sort = Sort.by(Arrays.asList(order));
        Pageable pageableRequest = PageRequest.of(page, 20, sort);
        return postRepository.findByPostTagsAndIsCancelled(tag, false, pageableRequest);
    }

    @Override
    public Page<Post> getPostsByAuthor(Pageable pageableRequest, Author author) {
        return postRepository.findByFullAuthorAndIsCancelled(author, false, pageableRequest);
    }

    @Override
    public Page<Post> getTopPostByDepartment(Pageable pageableRequest, PublishingDepartment department)
            throws OsirisException {
        return postRepository.findByPostCategoriesAndIsCancelledAndDepartments(getCategoryArticle(), false, department,
                pageableRequest);
    }

    @Override
    public Page<Post> getTopPostWithDepartment(Pageable pageableRequest)
            throws OsirisException {
        return postRepository.findByPostCategoriesWithDepartments(getCategoryArticle(), false,
                pageableRequest);
    }

    private Page<Post> getJssCategoryPostsByCategory(Pageable pageableRequest, Category category) {
        return postRepository.findJssCategoryPosts(category, false, pageableRequest);
    }

    @Override
    public Page<Post> getPostInterview(Pageable pageableRequest) throws OsirisException {
        return getJssCategoryPostsByCategory(pageableRequest, getCategoryInterview());
    }

    @Override
    public Page<Post> getPostsPodcast(Pageable pageableRequest) throws OsirisException {
        return postRepository.findByPostCategoriesAndIsCancelled(getCategoryPodcast(), false, pageableRequest);
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
        if (post != null && post.getJssCategories() != null && post.getJssCategories().size() > 0) {
            Order order = new Order(Direction.ASC, "date");
            Sort sort = Sort.by(Arrays.asList(order));
            Pageable pageableRequest = PageRequest.of(0, 1, sort);
            List<Post> posts = postRepository.findNextArticle(post.getJssCategories().get(0), post.getDate(),
                    pageableRequest);
            if (posts != null && posts.size() > 0)
                return posts.get(0);
        }
        return null;
    }

    @Override
    public Post getPreviousPost(Post post) {
        if (post != null && post.getJssCategories() != null && post.getJssCategories().size() > 0) {
            Order order = new Order(Direction.DESC, "date");
            Sort sort = Sort.by(Arrays.asList(order));
            Pageable pageableRequest = PageRequest.of(0, 1, sort);
            List<Post> posts = postRepository.findPreviousArticle(post.getJssCategories().get(0), post.getDate(),
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
    public Page<Post> applyPremium(Page<Post> posts) {
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reindexPosts() throws OsirisException {
        List<Post> posts = IterableUtils.toList(postRepository.findAll());
        if (posts != null)
            for (Post post : posts)
                batchService.declareNewBatch(Batch.REINDEX_POST, post.getId());
    }

    public List<Post> getPostExcludedId(List<Integer> postFetchedId) {

        return postRepository.findPostExcludIds(postFetchedId);
    }

    public void cancelPost(Post post) {
        post = getPost(post.getId());
        post.setIsCancelled(true);
        postRepository.save(post);
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
        if (post.getJss_category() != null && post.getJss_category().length > 0) {
            List<JssCategory> categories = new ArrayList<JssCategory>();
            List<JssCategory> availableCategories = jssCategoryService.getAvailableJssCategories();
            for (Integer i : post.getJss_category()) {
                for (JssCategory availableCategory : availableCategories) {
                    if (availableCategory.getId().equals(i)) {
                        categories.add(availableCategory);
                        break;
                    }
                }
            }
            post.setJssCategories(categories);
        }
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

}
