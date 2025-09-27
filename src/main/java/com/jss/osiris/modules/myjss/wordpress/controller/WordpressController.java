package com.jss.osiris.modules.myjss.wordpress.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.libs.mail.GeneratePdfDelegate;
import com.jss.osiris.libs.search.model.IndexEntity;
import com.jss.osiris.libs.search.service.SearchService;
import com.jss.osiris.modules.myjss.wordpress.model.AssoMailAuthor;
import com.jss.osiris.modules.myjss.wordpress.model.AssoMailJssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.AssoMailTag;
import com.jss.osiris.modules.myjss.wordpress.model.Author;
import com.jss.osiris.modules.myjss.wordpress.model.Category;
import com.jss.osiris.modules.myjss.wordpress.model.JssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.MyJssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.Newspaper;
import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.myjss.wordpress.model.PublishingDepartment;
import com.jss.osiris.modules.myjss.wordpress.model.ReadingFolder;
import com.jss.osiris.modules.myjss.wordpress.model.Serie;
import com.jss.osiris.modules.myjss.wordpress.model.Subscription;
import com.jss.osiris.modules.myjss.wordpress.model.Tag;
import com.jss.osiris.modules.myjss.wordpress.service.AssoMailAuthorService;
import com.jss.osiris.modules.myjss.wordpress.service.AssoMailJssCategoryService;
import com.jss.osiris.modules.myjss.wordpress.service.AssoMailTagService;
import com.jss.osiris.modules.myjss.wordpress.service.AuthorService;
import com.jss.osiris.modules.myjss.wordpress.service.CategoryService;
import com.jss.osiris.modules.myjss.wordpress.service.JssCategoryService;
import com.jss.osiris.modules.myjss.wordpress.service.MyJssCategoryService;
import com.jss.osiris.modules.myjss.wordpress.service.NewspaperService;
import com.jss.osiris.modules.myjss.wordpress.service.PostService;
import com.jss.osiris.modules.myjss.wordpress.service.PostViewService;
import com.jss.osiris.modules.myjss.wordpress.service.PublishingDepartmentService;
import com.jss.osiris.modules.myjss.wordpress.service.ReadingFolderService;
import com.jss.osiris.modules.myjss.wordpress.service.SerieService;
import com.jss.osiris.modules.myjss.wordpress.service.SubscriptionService;
import com.jss.osiris.modules.myjss.wordpress.service.TagService;
import com.jss.osiris.modules.osiris.crm.model.Comment;
import com.jss.osiris.modules.osiris.crm.service.CommentService;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.MailService;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.model.Announcement;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.service.AnnouncementService;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class WordpressController {

	private static final String inputEntryPoint = "myjss/wordpress";

	private final ConcurrentHashMap<String, AtomicLong> requestCount = new ConcurrentHashMap<>();
	private final long rateLimit = 10;
	private LocalDateTime lastFloodFlush = LocalDateTime.now();
	private int floodFlushDelayMinute = 1;

	@Autowired
	ValidationHelper validationHelper;

	@Autowired
	PublishingDepartmentService publishingDepartmentService;

	@Autowired
	JssCategoryService jssCategoryService;

	@Autowired
	MyJssCategoryService myJssCategoryService;

	@Autowired
	CategoryService categoryService;

	@Autowired
	SerieService serieService;

	@Autowired
	TagService tagService;

	@Autowired
	PostService postService;

	@Autowired
	PostViewService postViewService;

	@Autowired
	AuthorService authorService;

	@Autowired
	SearchService searchService;

	@Autowired
	AnnouncementService announcementService;

	@Autowired
	CommentService commentService;

	@Autowired
	MailService mailService;

	@Autowired
	ConstantService constantService;

	@Autowired
	CustomerOrderService customerOrderService;

	@Autowired
	SubscriptionService subscriptionService;

	@Autowired
	GeneratePdfDelegate generatePdfDelegate;

	@Autowired
	AssoMailAuthorService assoMailAuthorService;

	@Autowired
	AssoMailTagService assoMailTagService;

	@Autowired
	AssoMailJssCategoryService assoMailJssCategoryService;

	@Autowired
	ReadingFolderService readingFolderService;

	@Autowired
	NewspaperService newspaperService;

	@Autowired
	EmployeeService employeeService;

	// Crawler user-agents
	private static final List<String> CRAWLER_USER_AGENTS = Arrays.asList("Googlebot", "Bingbot", "Slurp",
			"DuckDuckBot", "Baiduspider", "YandexBot", "Sogou", "Exabot", "facebot", "ia_archiver");

	private boolean isCrawler(HttpServletRequest request) {
		return CRAWLER_USER_AGENTS.stream().anyMatch(request.getHeader("User-Agent")::contains);
	}

	@GetMapping(inputEntryPoint + "/publishing-departments")
	public ResponseEntity<List<PublishingDepartment>> getAvailableDepartments() {
		return new ResponseEntity<List<PublishingDepartment>>(publishingDepartmentService.getAvailableDepartments(),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/publishing-department")
	public ResponseEntity<PublishingDepartment> getPublishingDepartmentByCode(
			@Param("departmentCode") String departmentCode) {
		return new ResponseEntity<PublishingDepartment>(
				publishingDepartmentService.getPublishingDepartment(departmentCode),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/jss-categories")
	public ResponseEntity<List<JssCategory>> getAvailableJssCategories() {
		return new ResponseEntity<List<JssCategory>>(jssCategoryService.getAvailableJssCategories(),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/jss-category/slug")
	public ResponseEntity<JssCategory> getJssCategoryBySlug(@RequestParam("slug") String slug) {
		return new ResponseEntity<JssCategory>(jssCategoryService.getJssCategoryBySlug(slug), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/categories")
	public ResponseEntity<List<Category>> getAvailableCategories() {
		return new ResponseEntity<List<Category>>(categoryService.getAvailableCategories(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/series")
	public ResponseEntity<Page<Serie>> getSeries(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			HttpServletRequest request) {

		detectFlood(request);

		Pageable pageable = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "serieOrder"));

		return new ResponseEntity<Page<Serie>>(
				serieService.getSeries(pageable), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/serie/slug")
	public ResponseEntity<Serie> getSerieBySlug(@RequestParam String slug) {
		return new ResponseEntity<Serie>(serieService.getSerieBySlug(slug), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/author/slug")
	public ResponseEntity<Author> getAuthorBySlug(@RequestParam String slug) {
		return new ResponseEntity<Author>(authorService.getAuthorBySlug(slug), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/post/bookmark/add")
	public ResponseEntity<Boolean> addBookmarkPost(@RequestParam Integer idPost,
			@RequestParam(required = false) Integer idReadingFolder,
			HttpServletRequest request) throws OsirisException {

		detectFlood(request);

		Post post = postService.getPost(idPost);

		if (post == null)
			throw new OsirisValidationException("post");

		Responsable responsable = employeeService.getCurrentMyJssUser();
		if (responsable == null || responsable.getMail() == null)
			throw new OsirisValidationException("responsable");

		ReadingFolder readingFolder = null;
		if (idReadingFolder != null)
			readingFolder = readingFolderService.getReadingFolder(idReadingFolder);
		if (readingFolder != null && readingFolder.getMail().getId().equals(responsable.getMail().getId())) {
			postService.updateBookmarkPost(post, readingFolder, responsable);
			return new ResponseEntity<Boolean>(true, HttpStatus.OK);
		}
		return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
	}

	@GetMapping(inputEntryPoint + "/post/bookmark/delete")
	public ResponseEntity<Boolean> deleteBookmarkPost(@RequestParam Integer idPost,
			HttpServletRequest request) throws OsirisException {

		detectFlood(request);

		Post post = postService.getPost(idPost);

		if (post == null)
			throw new OsirisValidationException("post");

		Responsable responsable = employeeService.getCurrentMyJssUser();
		if (responsable == null)
			throw new OsirisValidationException("responsable");

		postService.deleteBookmarkPost(post, responsable);
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/post/bookmark/all")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Post>> getBookmarkPostsForCurrentUser(
			@RequestParam Integer idReadingFolder,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			HttpServletRequest request) throws OsirisException {

		detectFlood(request);
		ReadingFolder readingFolder = readingFolderService.getReadingFolder(idReadingFolder);

		if (readingFolder == null)
			throw new OsirisValidationException("readingFolder");

		Responsable responsable = employeeService.getCurrentMyJssUser();
		if (responsable == null)
			throw new OsirisValidationException("responsable");

		Pageable pageable = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "date"));

		return new ResponseEntity<Page<Post>>(
				postService.getBookmarkPostsByReadingFolderForCurrentUser(readingFolder, responsable, pageable),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/author/follow/add")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<AssoMailAuthor> followAuthorForUser(@RequestParam Integer idAuthor,
			HttpServletRequest request) throws OsirisException {

		detectFlood(request);

		Author author = authorService.getAuthor(idAuthor);

		if (author == null)
			throw new OsirisValidationException("author");

		return new ResponseEntity<AssoMailAuthor>(assoMailAuthorService.addNewAuthorFollow(author),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/author/follow/get")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Boolean> getAssoMailAuthor(@RequestParam Integer idAuthor,
			HttpServletRequest request) throws OsirisException {

		detectFlood(request);

		Author author = authorService.getAuthor(idAuthor);

		if (author == null)
			throw new OsirisValidationException("author");

		return new ResponseEntity<Boolean>(
				assoMailAuthorService.getIsAssoMailAuthorByMailAndAuthor(author),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/author/unfollow")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Boolean> unfollowAuthor(@RequestParam Integer idAuthor,
			HttpServletRequest request) throws OsirisException {

		detectFlood(request);
		if (idAuthor == null)
			throw new OsirisValidationException("idAssoMailAuthor");

		Author author = authorService.getAuthor(idAuthor);
		assoMailAuthorService.deleteAssoMailAuthor(author);
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/tag/follow/add")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<AssoMailTag> followTagForUser(@RequestParam Integer idTag,
			HttpServletRequest request) throws OsirisException {

		detectFlood(request);

		Tag tag = tagService.getTag(idTag);

		if (tag == null)
			throw new OsirisValidationException("tag");

		return new ResponseEntity<AssoMailTag>(assoMailTagService.addNewTagFollow(tag),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/tag/follow/get")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Boolean> getAssoMailTag(@RequestParam Integer idTag,
			HttpServletRequest request) throws OsirisException {

		detectFlood(request);

		Tag tag = tagService.getTag(idTag);

		if (tag == null)
			throw new OsirisValidationException("tag");

		return new ResponseEntity<Boolean>(
				assoMailTagService.getIsAssoMailTagByMailAndTag(tag),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/tag/unfollow")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Boolean> unfollowTag(@RequestParam Integer idTag,
			HttpServletRequest request) throws OsirisException {

		detectFlood(request);

		if (idTag == null)
			throw new OsirisValidationException("idTag");

		Tag tag = tagService.getTag(idTag);

		assoMailTagService.deleteAssoMailTag(tag);
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/jss-category/follow/add")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<AssoMailJssCategory> followJssCategoryForUser(@RequestParam Integer idJssCategory,
			HttpServletRequest request) throws OsirisException {

		detectFlood(request);

		JssCategory jssCategory = jssCategoryService.getJssCategory(idJssCategory);

		if (jssCategory == null)
			throw new OsirisValidationException("jssCategory");

		return new ResponseEntity<AssoMailJssCategory>(assoMailJssCategoryService.addNewJssCategoryFollow(jssCategory),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/jss-category/follow/get")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Boolean> getAssoMailJssCategory(@RequestParam Integer idJssCategory,
			HttpServletRequest request) throws OsirisException {

		detectFlood(request);

		JssCategory jssCategory = jssCategoryService.getJssCategory(idJssCategory);

		if (jssCategory == null)
			throw new OsirisValidationException("jssCategory");

		return new ResponseEntity<Boolean>(
				assoMailJssCategoryService.getIsAssoMailJssCategoryByMailAndJssCategory(jssCategory),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/jss-category/unfollow")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Boolean> unfollowJssCategory(@RequestParam Integer idJssCategory,
			HttpServletRequest request) throws OsirisException {

		detectFlood(request);

		JssCategory jssCategory = jssCategoryService.getJssCategory(idJssCategory);

		if (jssCategory == null)
			throw new OsirisValidationException("jssCategory");

		assoMailJssCategoryService.deleteAssoMailJssCategory(jssCategory);
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/jss/last")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Post>> getLastJssPosts(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String searchText,
			HttpServletRequest request)
			throws OsirisException {

		detectFlood(request);

		Pageable pageable = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "date"));

		return new ResponseEntity<Page<Post>>(
				postService.computeBookmarkedPosts(postService.getJssCategoryPosts(searchText, pageable)),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/myjss/top")
	public ResponseEntity<List<Post>> getTopMyJssPosts(@RequestParam Integer page) throws OsirisException {
		return new ResponseEntity<List<Post>>(postService.getMyJssCategoryPosts(page),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/jss/tendency")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Post>> getJssCategoryPostsTendency(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String searchText,
			HttpServletRequest request) throws OsirisException {
		detectFlood(request);

		Pageable pageable = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "date"));
		return new ResponseEntity<Page<Post>>(postService.getJssCategoryPostTendency(searchText, pageable),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/myjss/tendency")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<List<Post>> getMyJssCategoryPostsTendency() throws OsirisException {
		return new ResponseEntity<List<Post>>(postService.getMyJssCategoryPostTendency(),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/myjss/most-seen")
	public ResponseEntity<List<Post>> getMyJssPostsMostSeen() throws OsirisException {
		return new ResponseEntity<List<Post>>(postService.getMyJssCategoryPostMostSeen(),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/most-seen")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Post>> getMostSeenJssPosts(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			HttpServletRequest request) throws OsirisException {

		detectFlood(request);

		Pageable pageableRequest = PageRequest.of(page, ValidationHelper.limitPageSize(size));

		return new ResponseEntity<Page<Post>>(
				postService.getJssCategoryPostMostSeen(pageableRequest),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/pinned")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Post>> getPinnedPosts(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			HttpServletRequest request) throws OsirisException {

		detectFlood(request);

		Pageable pageableRequest = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "date"));

		return new ResponseEntity<Page<Post>>(
				postService.getJssCategoryStickyPost(pageableRequest),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/myjss/pinned")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Post>> getMyJssPinnedPosts(@RequestParam Integer myJssCategoryId,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			HttpServletRequest request) throws OsirisException {

		detectFlood(request);

		Pageable pageableRequest = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "date"));

		MyJssCategory myJssCategory = myJssCategoryService.getMyJssCategory(myJssCategoryId);

		if (myJssCategory == null)
			throw new OsirisValidationException("myJssCategory");

		return new ResponseEntity<Page<Post>>(
				postService.getMyJssCategoryStickyPost(pageableRequest, myJssCategory),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/slug")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Post> getPostBySlug(@RequestParam String slug, HttpServletRequest request)
			throws OsirisException {
		if (slug != null && slug.contains("%"))
			slug = URLDecoder.decode(slug, StandardCharsets.UTF_8);
		Post post = postService.getPostsBySlug(slug);
		if (post == null && slug.matches("[0-9]+") && slug.length() > 2) {
			// For legacy post by id
			post = postService.getPost(Integer.parseInt("100000" + slug));
		}
		if (post != null && !isCrawler(request))
			postViewService.incrementView(post);
		return new ResponseEntity<Post>(postService.applyPremiumAndBookmarks(post, null, null, false), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/slug/token")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Post> getPostBySlugWithToken(String validationToken, String mail,
			HttpServletRequest request)
			throws OsirisException {
		Subscription subscription = subscriptionService.getSubscriptionByToken(validationToken);
		if (subscription == null)
			throw new OsirisValidationException("validationToken");

		if (subscription.getPost() != null && !isCrawler(request))
			postViewService.incrementView(subscription.getPost());
		return new ResponseEntity<Post>(
				postService.applyPremiumAndBookmarks(subscription.getPost(), validationToken, mail, false),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/serie/slug")
	@JsonView({ JacksonViews.MyJssListView.class })
	public ResponseEntity<List<Post>> getPostSerieBySlug(@RequestParam String slug, HttpServletRequest request)
			throws OsirisException {
		Serie serie = serieService.getSerieBySlug(slug);
		if (serie == null)
			return new ResponseEntity<List<Post>>(new ArrayList<Post>(), HttpStatus.OK);
		return new ResponseEntity<List<Post>>(postService.getPostBySerie(serie),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/top/jss-category")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Post>> getTopPostByJssCategory(
			@RequestParam(required = false) Integer categoryId,
			@RequestParam(required = false, defaultValue = "0") Integer page,
			@RequestParam(required = false, defaultValue = "10") Integer size) {

		JssCategory category = jssCategoryService.getJssCategory(categoryId);
		if (category == null)
			return new ResponseEntity<>(new PageImpl<>(Collections.emptyList()), HttpStatus.OK);

		Pageable pageable = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "date"));

		return new ResponseEntity<Page<Post>>(
				postService.getPostsByJssCategory(pageable, category), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/jss-category/most-seen")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Post>> getMostSeenPostByJssCatgory(
			@RequestParam Integer jssCategoryId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			HttpServletRequest request) {
		detectFlood(request);
		Pageable pageable = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "date"));
		JssCategory category = jssCategoryService.getJssCategory(jssCategoryId);

		if (category == null)
			return new ResponseEntity<>(new PageImpl<>(Collections.emptyList()), HttpStatus.OK);

		return new ResponseEntity<Page<Post>>(
				postService.getMostSeenPostByJssCatgory(pageable, category),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/tag/most-seen")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Post>> getMostSeenPostByTag(
			@RequestParam String tagSlug, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			HttpServletRequest request) {
		detectFlood(request);
		Pageable pageable = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "date"));
		Tag tag = tagService.getTagBySlug(tagSlug);

		if (tag == null)
			return new ResponseEntity<>(new PageImpl<>(Collections.emptyList()), HttpStatus.OK);

		return new ResponseEntity<Page<Post>>(
				postService.getMostSeenPostByTag(pageable, tag),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/author/most-seen")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Post>> getMostSeenPostByAuthor(
			@RequestParam String authorSlug, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			HttpServletRequest request) {
		detectFlood(request);
		Pageable pageable = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "date"));

		Author author = authorService.getAuthorBySlug(authorSlug);

		if (author == null)
			return new ResponseEntity<>(new PageImpl<>(Collections.emptyList()), HttpStatus.OK);

		return new ResponseEntity<Page<Post>>(
				postService.getMostSeenPostByAuthor(pageable, author),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/serie/most-seen")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Post>> getMostSeenPostBySerie(
			@RequestParam String serieSlug, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			HttpServletRequest request) {
		detectFlood(request);
		Pageable pageable = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "date"));
		Serie serie = serieService.getSerieBySlug(serieSlug);

		if (serie == null)
			return new ResponseEntity<>(new PageImpl<>(Collections.emptyList()), HttpStatus.OK);

		return new ResponseEntity<Page<Post>>(
				postService.getMostSeenPostBySerie(pageable, serie),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/premium/most-seen")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Post>> getMostSeenPremiumPost(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size, HttpServletRequest request) {
		detectFlood(request);
		Pageable pageable = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "date"));

		return new ResponseEntity<Page<Post>>(
				postService.getMostSeenPremiumPost(pageable),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/publishing-department/most-seen")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Post>> getMostSeenPostByPublishingDepartment(
			@RequestParam String departmentCode, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			HttpServletRequest request) throws OsirisException {
		detectFlood(request);
		Pageable pageable = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "date"));

		PublishingDepartment publishingDepartment = publishingDepartmentService.getPublishingDepartment(departmentCode);

		if (publishingDepartment == null)
			return new ResponseEntity<>(new PageImpl<>(Collections.emptyList()), HttpStatus.OK);

		if (publishingDepartment.getId().equals(constantService.getPublishingDepartmentIdf().getId()))
			return new ResponseEntity<Page<Post>>(
					postService.getMostSeenPostByIdf(pageable),
					HttpStatus.OK);

		return new ResponseEntity<Page<Post>>(
				postService.getMostSeenPostByPublishingDepartment(pageable, publishingDepartment),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/all/jss-category")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Post>> getAllPostsByJssCategory(
			@RequestParam Integer categoryId,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestParam(required = false) String searchText,
			@RequestParam Boolean isDisplayNewPosts,
			HttpServletRequest request) {

		detectFlood(request);
		Pageable pageableRequest = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "date"));

		JssCategory jssCategory = jssCategoryService.getJssCategory(categoryId);

		if (jssCategory == null)
			return new ResponseEntity<>(new PageImpl<>(Collections.emptyList()), HttpStatus.OK);

		return new ResponseEntity<Page<Post>>(
				postService
						.applyPremiumAndBookmarks(
								postService.getAllPostsByJssCategory(pageableRequest, jssCategory, searchText,
										computeJssCategoryConsultationDate(isDisplayNewPosts, jssCategory))),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/all/category")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Post>> getAllPostsByCategory(
			@RequestParam Integer categoryId,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			HttpServletRequest request) {

		detectFlood(request);
		Pageable pageableRequest = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "date"));

		Category category = categoryService.getCategory(categoryId);

		if (category == null)
			return new ResponseEntity<>(new PageImpl<>(Collections.emptyList()), HttpStatus.OK);

		return new ResponseEntity<Page<Post>>(
				postService.getAllPostsByCategory(pageableRequest, category),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/all/tag")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Post>> getAllPostsByTag(
			@RequestParam String tagSlug,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestParam Boolean isDisplayNewPosts,
			@RequestParam(required = false) String searchText,
			HttpServletRequest request) throws OsirisException {

		detectFlood(request);

		Pageable pageableRequest = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "date"));

		Tag tag = tagService.getTagBySlug(tagSlug);

		if (tag == null)
			return new ResponseEntity<>(new PageImpl<>(Collections.emptyList()), HttpStatus.OK);

		return new ResponseEntity<Page<Post>>(
				postService
						.applyPremiumAndBookmarks(postService.getAllPostsByTag(pageableRequest, tag, searchText,
								computeTagConsultationDate(isDisplayNewPosts, tag))),
				HttpStatus.OK);

	}

	@GetMapping(inputEntryPoint + "/posts/all/author")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Post>> getAllPostsByAuthor(
			@RequestParam String authorSlug,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestParam Boolean isDisplayNewPosts,
			@RequestParam(required = false) String searchText,
			HttpServletRequest request) {

		detectFlood(request);

		Pageable pageableRequest = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "date"));

		Author author = authorService.getAuthorBySlug(authorSlug);

		if (author == null)
			return new ResponseEntity<>(new PageImpl<>(Collections.emptyList()), HttpStatus.OK);

		return new ResponseEntity<Page<Post>>(
				postService
						.applyPremiumAndBookmarks(postService.getAllPostsByAuthor(pageableRequest, author, searchText,
								computeAuthorConsultationDate(isDisplayNewPosts, author))),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/all/serie")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Post>> getAllPostsBySerie(
			@RequestParam String serieSlug,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size, @RequestParam(required = false) String searchText,
			HttpServletRequest request) {

		detectFlood(request);

		Pageable pageableRequest = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "date"));

		Serie serie = serieService.getSerieBySlug(serieSlug);

		if (serie == null)
			return new ResponseEntity<>(new PageImpl<>(Collections.emptyList()), HttpStatus.OK);

		return new ResponseEntity<Page<Post>>(
				postService
						.applyPremiumAndBookmarks(postService.getAllPostsBySerie(pageableRequest, serie, searchText)),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/all/publishing-department")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Post>> getAllPostsByPublishingDepartment(
			@RequestParam String departmentCode,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size, @RequestParam(required = false) String searchText,
			HttpServletRequest request) throws OsirisException {

		detectFlood(request);

		Pageable pageableRequest = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "date"));

		PublishingDepartment publishingDepartment = publishingDepartmentService.getPublishingDepartment(departmentCode);

		if (publishingDepartment == null)
			return new ResponseEntity<>(new PageImpl<>(Collections.emptyList()), HttpStatus.OK);

		if (publishingDepartment.getId().equals(constantService.getPublishingDepartmentIdf().getId()))
			return new ResponseEntity<Page<Post>>(
					postService.getAllPostsByIdf(pageableRequest, searchText),
					HttpStatus.OK);

		return new ResponseEntity<Page<Post>>(
				postService
						.applyPremiumAndBookmarks(
								postService.getAllPostsByPublishingDepartment(pageableRequest, publishingDepartment,
										searchText)),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/all/premium")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Post>> getAllPremiumPosts(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size, @RequestParam(required = false) String searchText,
			HttpServletRequest request) throws OsirisException {

		detectFlood(request);

		Pageable pageableRequest = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "date"));

		return new ResponseEntity<Page<Post>>(
				postService
						.applyPremiumAndBookmarks(postService.getAllPremiumPosts(searchText, pageableRequest)),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/top/myjss-category")
	public ResponseEntity<Page<Post>> getTopPostByMyJssCategory(@RequestParam Integer page,
			@RequestParam Integer myJssCategoryId) {
		MyJssCategory myJssCategory = myJssCategoryService.getMyJssCategory(myJssCategoryId);
		if (myJssCategory == null)
			return new ResponseEntity<>(new PageImpl<>(Collections.emptyList()), HttpStatus.OK);

		return new ResponseEntity<Page<Post>>(postService.getPostsByMyJssCategory(page, myJssCategory), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/myjss-categories")
	public ResponseEntity<List<MyJssCategory>> getAvailableMyJssCategories() {
		return new ResponseEntity<List<MyJssCategory>>(
				myJssCategoryService.getAvailableMyJssCategories(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/first-myjss-category")
	public ResponseEntity<List<Post>> getFirstPostsByMyJssCategories(@RequestParam(required = false) String searchText,
			@RequestParam(required = false) Integer myJssCategoryId, HttpServletRequest request) {
		detectFlood(request);
		MyJssCategory myJssCategory = null;
		if (myJssCategoryId != null)
			myJssCategory = myJssCategoryService.getMyJssCategory(myJssCategoryId);

		return new ResponseEntity<List<Post>>(postService.getFirstPostsByMyJssCategories(myJssCategory),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/search/myjss-category")
	public ResponseEntity<Page<Post>> searchPostsByMyJssCategory(@RequestParam String searchText,
			@RequestParam(required = false) Integer myJssCategoryId,
			@RequestParam(required = false, defaultValue = "0") Integer page,
			@RequestParam(required = false, defaultValue = "10") Integer size,
			HttpServletRequest request) {
		detectFlood(request);

		if (searchText == null || searchText.trim().length() == 0)
			searchText = "";
		searchText = searchText.trim().toLowerCase();

		MyJssCategory myJssCategory = myJssCategoryService.getMyJssCategory(myJssCategoryId);

		Order order = new Order(Direction.DESC, "titleText");
		Sort sort = Sort.by(Arrays.asList(order));
		Pageable pageableRequest = PageRequest.of(page, ValidationHelper.limitPageSize(size), sort);

		return new ResponseEntity<Page<Post>>(
				postService.searchPostsByMyJssCategory(searchText, myJssCategory, pageableRequest), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/search/posts/category")
	public ResponseEntity<Page<Post>> searchPostsByCategory(@RequestParam(required = false) String searchText,
			@RequestParam Integer categoryId,
			@RequestParam(required = false, defaultValue = "0") Integer page,
			@RequestParam(required = false, defaultValue = "10") Integer size,
			HttpServletRequest request) {
		detectFlood(request);

		if (searchText == null || searchText.trim().length() == 0)
			searchText = "";
		searchText = searchText.trim().toLowerCase();

		Category categoryExclusive = categoryService.getCategory(categoryId);

		Order order = new Order(Direction.DESC, "date");
		Sort sort = Sort.by(Arrays.asList(order));
		Pageable pageableRequest = PageRequest.of(page, ValidationHelper.limitPageSize(size), sort);

		return new ResponseEntity<Page<Post>>(
				postService.searchPostsByCategory(searchText, categoryExclusive, pageableRequest), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/myjss-category")
	public ResponseEntity<Page<Post>> getPostsByMyJssCategory(@RequestParam Integer myJssCategoryId,
			@RequestParam(required = false) String searchText,
			@RequestParam(required = false, defaultValue = "0") Integer page,
			@RequestParam(required = false, defaultValue = "10") Integer size,
			HttpServletRequest request) {
		detectFlood(request);

		MyJssCategory myJssCategory = myJssCategoryService.getMyJssCategory(myJssCategoryId);

		if (myJssCategory == null)
			return new ResponseEntity<>(new PageImpl<>(Collections.emptyList()), HttpStatus.OK);

		Order order = new Order(Direction.DESC, "date");
		Sort sort = Sort.by(Arrays.asList(order));
		Pageable pageableRequest = PageRequest.of(page, ValidationHelper.limitPageSize(size), sort);

		return new ResponseEntity<Page<Post>>(
				postService.searchPostsByMyJssCategory(searchText, myJssCategory, pageableRequest), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/post/next")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Post> getNextPost(@RequestParam Integer idPost) {
		Post post = postService.getPost(idPost);
		if (post == null)
			return new ResponseEntity<Post>(new Post(), HttpStatus.OK);
		return new ResponseEntity<Post>(
				postService.applyPremiumAndBookmarks(postService.getNextPost(post), null, null, true),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/post/previous")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Post> getPreviousPost(@RequestParam Integer idPost) {
		Post post = postService.getPost(idPost);
		if (post == null)
			return new ResponseEntity<Post>(new Post(), HttpStatus.OK);
		return new ResponseEntity<Post>(
				postService.applyPremiumAndBookmarks(postService.getPreviousPost(post), null, null, true),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/post/get")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Post> getPostById(@RequestParam Integer idPost) {

		if (postService.getPost(idPost) == null)
			return new ResponseEntity<Post>(new Post(), HttpStatus.OK);
		return new ResponseEntity<Post>(
				postService.applyPremiumAndBookmarks(postService.getPost(idPost), null, null, false),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/top/department")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Post>> getTopPostByDepartment(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam String departmentCode,
			HttpServletRequest request) throws OsirisException {

		Pageable pageable = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "date"));

		PublishingDepartment department = publishingDepartmentService.getPublishingDepartment(departmentCode);
		if (department == null)
			return new ResponseEntity<>(new PageImpl<>(Collections.emptyList()), HttpStatus.OK);

		return new ResponseEntity<Page<Post>>(
				postService.getTopPostByDepartment(pageable, department), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/top/department/all")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Post>> getTopPostWithDepartment(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			HttpServletRequest request) throws OsirisException {

		Pageable pageable = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "date"));

		return new ResponseEntity<Page<Post>>(postService.getTopPostWithDepartment(pageable),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/top/podcast")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Post>> getTopPostPodcast(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			HttpServletRequest request) throws OsirisException {

		detectFlood(request);

		Pageable pageable = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "date"));

		return new ResponseEntity<Page<Post>>(postService.getPostsPodcast(pageable), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/tags")
	public ResponseEntity<List<Tag>> getAllTags() {
		return new ResponseEntity<List<Tag>>(tagService.getAvailableTags(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/tag/slug")
	public ResponseEntity<Tag> getTagBySlug(@RequestParam String slug) {
		return new ResponseEntity<Tag>(tagService.getTagBySlug(slug), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/tags/all/jss-category")
	public ResponseEntity<List<Tag>> getAllTagsByJssCategory(@RequestParam Integer jssCategoryId) {

		if (jssCategoryId == null)
			return new ResponseEntity<List<Tag>>(new ArrayList<Tag>(), HttpStatus.OK);

		JssCategory category = jssCategoryService.getJssCategory(jssCategoryId);

		return new ResponseEntity<List<Tag>>(tagService.getAllTagsByJssCategory(category), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/tags/all/category")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Tag>> getAllTagsByJssCategory(
			@RequestParam Integer categoryId,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			HttpServletRequest request) {

		detectFlood(request);
		Pageable pageableRequest = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "date"));

		Category category = categoryService.getCategory(categoryId);

		if (category == null)
			return new ResponseEntity<>(new PageImpl<>(Collections.emptyList()), HttpStatus.OK);

		return new ResponseEntity<Page<Tag>>(
				tagService.getAllTagsByCategory(pageableRequest, category),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/tags/all/tag")
	public ResponseEntity<List<Tag>> getAllTagsByTag(@RequestParam String tagSlug,
			@RequestParam Boolean isDisplayNewPosts) throws OsirisException {

		if (tagSlug == null)
			return new ResponseEntity<List<Tag>>(new ArrayList<Tag>(), HttpStatus.OK);

		Tag tag = tagService.getTagBySlug(tagSlug);

		return new ResponseEntity<List<Tag>>(
				tagService.getAllTagsByTag(tag, computeTagConsultationDate(isDisplayNewPosts, tag)), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/tags/all/author")
	public ResponseEntity<List<Tag>> getAllTagsByAuthor(@RequestParam String authorSlug,
			@RequestParam Boolean isDisplayNewPosts) {

		if (authorSlug == null)
			return new ResponseEntity<List<Tag>>(new ArrayList<Tag>(), HttpStatus.OK);

		Author author = authorService.getAuthorBySlug(authorSlug);

		return new ResponseEntity<List<Tag>>(tagService.getAllTagsByAuthor(author,
				computeAuthorConsultationDate(isDisplayNewPosts, author)), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/tags/all/serie")
	public ResponseEntity<List<Tag>> getAllTagsBySerie(@RequestParam String serieSlug) {

		if (serieSlug == null)
			return new ResponseEntity<List<Tag>>(new ArrayList<Tag>(), HttpStatus.OK);

		Serie serie = serieService.getSerieBySlug(serieSlug);

		return new ResponseEntity<List<Tag>>(tagService.getAllTagsBySerie(serie), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/tags/tendency")
	public ResponseEntity<List<Tag>> getAllTendencyTags() throws OsirisException {
		return new ResponseEntity<List<Tag>>(tagService.getAllTendencyTags(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/tags/last")
	public ResponseEntity<List<Tag>> getAllLastPostsTags() throws OsirisException {
		return new ResponseEntity<List<Tag>>(tagService.getAllTendencyTags(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/tags/most-seen")
	public ResponseEntity<List<Tag>> getAllMostSeenPostsTags() throws OsirisException {
		return new ResponseEntity<List<Tag>>(tagService.getAllMostSeenPostsTags(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/tags/all/publishing-department")
	public ResponseEntity<List<Tag>> getAllTagsByPublishingDepartment(@RequestParam String departmentCode)
			throws OsirisException {

		if (departmentCode == null)
			return new ResponseEntity<List<Tag>>(new ArrayList<Tag>(), HttpStatus.OK);

		PublishingDepartment publishingDepartment = publishingDepartmentService.getPublishingDepartment(departmentCode);

		if (publishingDepartment.getId().equals(constantService.getPublishingDepartmentIdf().getId()))
			return new ResponseEntity<List<Tag>>(tagService.getAllTagsByIdf(), HttpStatus.OK);

		return new ResponseEntity<List<Tag>>(tagService.getAllTagsByPublishingDepartment(publishingDepartment),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/tags/all/premium")
	public ResponseEntity<List<Tag>> getAllTagsByPremiumPosts()
			throws OsirisException {

		return new ResponseEntity<List<Tag>>(tagService.getAllTagsByPremiumPosts(),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/search/post")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<List<IndexEntity>> globalSearchForEntity(@RequestParam String searchText)
			throws OsirisException {
		// TODO : leak premium
		if (searchText != null && searchText.length() > 2)
			return new ResponseEntity<List<IndexEntity>>(
					searchService.searchForEntities(searchText, Post.class.getSimpleName(), false),
					HttpStatus.OK);
		return new ResponseEntity<List<IndexEntity>>(new ArrayList<IndexEntity>(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/announcement/search")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Announcement>> getTopAnnouncementSearch(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int pageSize, @RequestParam(required = false) String searchText,
			HttpServletRequest request) throws OsirisException {

		detectFlood(request);

		Pageable pageable = PageRequest.of(page, ValidationHelper.limitPageSize(pageSize),
				Sort.by(Sort.Direction.DESC, "publicationDate"));

		if (searchText == null || searchText.trim().length() == 0)
			searchText = "";
		searchText = searchText.trim().toLowerCase();

		return new ResponseEntity<Page<Announcement>>(
				announcementService.getAnnouncementSearch(searchText, null, pageable), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/announcement/last-seven-days")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<List<Announcement>> getLastSevenDaysAnnouncements(HttpServletRequest request)
			throws OsirisException {

		detectFlood(request);

		return new ResponseEntity<List<Announcement>>(
				announcementService.getLastSevenDaysAnnouncements(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/announcement/unique")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Announcement> getAnnouncement(@RequestParam Integer announcementId,
			HttpServletRequest request)
			throws OsirisException {
		detectFlood(request);

		Announcement announcement = announcementService.getAnnouncement(announcementId);

		if (announcement == null)
			return new ResponseEntity<Announcement>(new Announcement(), HttpStatus.OK);

		return new ResponseEntity<Announcement>(announcementService.getAnnouncementForWebSite(announcement),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/publication/flag/download")
	public ResponseEntity<byte[]> downloadPublicationFlag(@RequestParam("idAnnouncement") Integer idAnnouncement)
			throws OsirisValidationException, OsirisException {
		byte[] data = null;
		HttpHeaders headers = null;

		Announcement announcement = announcementService.getAnnouncement(idAnnouncement);

		if (announcement == null)
			throw new OsirisValidationException("Annonce non trouvée");

		Provision provision = null;

		CustomerOrder customerOrder = customerOrderService.getCustomerOrderForAnnouncement(announcement);
		// Get provision
		if (customerOrder != null && customerOrder.getAssoAffaireOrders() != null)
			for (AssoAffaireOrder asso : customerOrder.getAssoAffaireOrders())
				for (Service service : asso.getServices())
					for (Provision orderProvision : service.getProvisions())
						if (orderProvision.getAnnouncement() != null
								&& orderProvision.getAnnouncement().getId().equals(announcement.getId())) {
							provision = orderProvision;
							break;
						}

		if (provision == null)
			throw new OsirisValidationException("Provision non trouvée");

		File file = generatePdfDelegate.generatePublicationForAnnouncement(announcement, provision, true, false, false);

		if (file != null) {
			try {
				data = Files.readAllBytes(file.toPath());
			} catch (IOException e) {
				throw new OsirisException(e, "Unable to read file " + file.getAbsolutePath());
			}

			headers = new HttpHeaders();
			headers.setContentLength(data.length);
			headers.add("filename", "Témoin de parution n°" + announcement.getId() + ".pdf");
			headers.setAccessControlExposeHeaders(Arrays.asList("filename"));

			// Compute content type
			String mimeType = null;
			try {
				mimeType = Files.probeContentType(file.toPath());
			} catch (IOException e) {
				throw new OsirisException(e, "Unable to read file " + file.getAbsolutePath());
			}
			if (mimeType == null)
				mimeType = "application/pdf";
			headers.set("content-type", mimeType);
			file.delete();
		}
		return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/post/comments")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Page<Comment>> getParentCommentsForPost(
			@RequestParam Integer postId,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			HttpServletRequest request) {

		detectFlood(request);

		Pageable pageable = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "creationDate"));

		return ResponseEntity.ok(commentService.getParentCommentsForPost(pageable, postId));
	}

	@PostMapping(inputEntryPoint + "/post/comment/add")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Comment> addOrUpdateComment(@RequestBody Comment comment,
			@RequestParam(value = "parentCommentId", required = false) Integer parentCommentId,
			@RequestParam Integer postId, HttpServletRequest request)
			throws OsirisException {

		detectFlood(request);

		if (comment != null && postId != null && comment.getMail() != null) {

			if (comment.getContent() == null || comment.getContent().trim() == "") {
				throw new OsirisValidationException("Impossible d'enregistrer un mail sans contenu");
			}

			if (!validationHelper.validateMail(comment.getMail())) {
				throw new OsirisValidationException("Le mail renseigné n'est pas valide !");
			}

			mailService.populateMailId(comment.getMail());

			if (postService.getPost(postId) != null) {
				return new ResponseEntity<Comment>(commentService.addOrUpdateComment(comment, parentCommentId, postId),
						HttpStatus.OK);
			} else {
				throw new OsirisValidationException("Trying to add a comment on a non-existing post");
			}
		}
		return new ResponseEntity<Comment>(new Comment(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/subscription/give-post")
	public ResponseEntity<Subscription> givePost(@RequestParam Integer postId, @RequestParam String recipientMailString,
			HttpServletRequest request)
			throws OsirisException {

		detectFlood(request);
		if (!validationHelper.validateMail(recipientMailString))
			throw new OsirisValidationException("Le mail renseigné n'est pas valide !");

		Mail recipientMail = new Mail(recipientMailString);
		recipientMail = mailService.populateMailId(recipientMail);

		Post postToOffer = postService.getPost(postId);
		if (postToOffer == null)
			throw new OsirisValidationException("Trying to offer a non-existing post");

		return new ResponseEntity<Subscription>(
				subscriptionService.givePostSubscription(postToOffer, recipientMail),
				HttpStatus.OK);
	}

	/**
	 * If response is null ==> the user cannot share any post
	 * 
	 * @param request
	 * @return
	 * @throws OsirisValidationException
	 */
	@GetMapping(inputEntryPoint + "/subscription/share-post-left")
	public ResponseEntity<Integer> getNumberOfRemainingPostsToShareForMonth(HttpServletRequest request)
			throws OsirisValidationException {

		detectFlood(request);

		Responsable currentUser = employeeService.getCurrentMyJssUser();

		if (currentUser == null) {
			throw new OsirisValidationException("Subscription e-mail does not exist");
		}

		return new ResponseEntity<Integer>(
				subscriptionService.getRemainingPostToShareForCurrentMonth(currentUser),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/newspapers/year")
	public ResponseEntity<List<Newspaper>> getNewspapersForYear(HttpServletRequest request, Integer year)
			throws OsirisValidationException, IOException {

		detectFlood(request);

		return new ResponseEntity<List<Newspaper>>(newspaperService.getNewspaperForYear(year), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/newspapers/can-see-all-newpapers")
	public ResponseEntity<Boolean> canSeeAllNewspapersOfKiosk(HttpServletRequest request)
			throws OsirisValidationException, IOException {

		detectFlood(request);

		Responsable responsable = employeeService.getCurrentMyJssUser();

		if (responsable == null) {
			return new ResponseEntity<Boolean>(false, HttpStatus.OK);
		}

		return new ResponseEntity<Boolean>(newspaperService.canSeeAllNewspapersOfKiosk(responsable), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/newspapers/list-seeable-newpapers")
	public ResponseEntity<List<Integer>> getSeeableNewspapersForCurrentUser(HttpServletRequest request)
			throws OsirisValidationException, IOException {

		detectFlood(request);

		Responsable responsable = employeeService.getCurrentMyJssUser();

		if (responsable == null) {
			return new ResponseEntity<List<Integer>>(new ArrayList<>(), HttpStatus.OK);
		}

		return new ResponseEntity<List<Integer>>(newspaperService.getSeeableNewspapersForResponsable(responsable),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/newspaper/download")
	public ResponseEntity<byte[]> getPdfForUser(@RequestParam("newspaperId") Integer newspaperId)
			throws OsirisValidationException, OsirisException {
		byte[] data = null;
		HttpHeaders headers = null;
		boolean canSeeFullNewspaper = false;

		Newspaper newspaper = newspaperService.getNewspaper(newspaperId);

		if (newspaper == null)
			throw new OsirisValidationException("Journal non trouvée");

		Responsable currentUser = employeeService.getCurrentMyJssUser();

		if (currentUser != null && subscriptionService.canResponsableSeeKioskNewspaper(currentUser, newspaper)) {
			canSeeFullNewspaper = true;
		}

		File file;
		if (canSeeFullNewspaper) {
			file = Paths.get(newspaper.getUploadedFullFile().getPath()).toFile();
		} else {
			file = Paths.get(newspaper.getUploadedCutFile().getPath()).toFile();
		}

		if (file != null) {
			try {
				data = Files.readAllBytes(file.toPath());
			} catch (IOException e) {
				throw new OsirisException(e, "Unable to read file " + file.getAbsolutePath());
			}

			headers = new HttpHeaders();
			headers.setContentLength(data.length);
			headers.add("filename",
					"JSS n°" + newspaper.getNewspaperIssueNumber() + (canSeeFullNewspaper ? "" : "_extrait") + ".pdf");
			headers.setAccessControlExposeHeaders(Arrays.asList("filename"));

			// Compute content type
			String mimeType = null;
			try {
				mimeType = Files.probeContentType(file.toPath());
			} catch (IOException e) {
				throw new OsirisException(e, "Unable to read file " + file.getAbsolutePath());
			}
			if (mimeType == null)
				mimeType = "application/pdf";
			headers.set("content-type", mimeType);
		}
		return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
	}

	private ResponseEntity<String> detectFlood(HttpServletRequest request) {
		if (lastFloodFlush.isBefore(LocalDateTime.now().minusMinutes(floodFlushDelayMinute)))
			requestCount.clear();

		String ipAddress = request.getRemoteAddr();
		AtomicLong count = requestCount.computeIfAbsent(ipAddress, k -> new AtomicLong());

		if (count.incrementAndGet() > rateLimit) {
			return new ResponseEntity<String>(new HttpHeaders(), HttpStatus.TOO_MANY_REQUESTS);
		}
		return null;
	}

	private LocalDateTime computeAuthorConsultationDate(Boolean isDisplayNewPosts,
			Author author) {
		AssoMailAuthor assoMailAuthor = null;

		if (isDisplayNewPosts) {
			assoMailAuthor = assoMailAuthorService.getAssoMailAuthorByMailAndAuthor(author);
			if (assoMailAuthor != null)
				return assoMailAuthor.getLastConsultationDate();
		}
		return LocalDateTime.of(1970, 1, 1, 0, 0);
	}

	private LocalDateTime computeTagConsultationDate(Boolean isDisplayNewPosts, Tag tag) {
		AssoMailTag assoMailTag = null;

		if (isDisplayNewPosts) {
			assoMailTag = assoMailTagService.getAssoMailTagByMailAndTag(tag);
			if (assoMailTag != null)
				return assoMailTag.getLastConsultationDate();
		}
		return LocalDateTime.of(1970, 1, 1, 0, 0);
	}

	private LocalDateTime computeJssCategoryConsultationDate(Boolean isDisplayNewPosts, JssCategory jssCategory) {
		AssoMailJssCategory assoMailJssCategory = null;

		if (isDisplayNewPosts) {
			assoMailJssCategory = assoMailJssCategoryService.getAssoMailJssCategoryByMailAndJssCategory(jssCategory);
			if (assoMailJssCategory != null)
				return assoMailJssCategory.getLastConsultationDate();
		}
		return LocalDateTime.of(1970, 1, 1, 0, 0);
	}
}
