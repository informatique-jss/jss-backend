package com.jss.osiris.modules.myjss.wordpress.controller;

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
import com.jss.osiris.libs.search.model.IndexEntity;
import com.jss.osiris.libs.search.service.SearchService;
import com.jss.osiris.modules.myjss.wordpress.model.Author;
import com.jss.osiris.modules.myjss.wordpress.model.Category;
import com.jss.osiris.modules.myjss.wordpress.model.JssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.MyJssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.myjss.wordpress.model.PublishingDepartment;
import com.jss.osiris.modules.myjss.wordpress.model.Serie;
import com.jss.osiris.modules.myjss.wordpress.model.Tag;
import com.jss.osiris.modules.myjss.wordpress.service.AuthorService;
import com.jss.osiris.modules.myjss.wordpress.service.CategoryService;
import com.jss.osiris.modules.myjss.wordpress.service.JssCategoryService;
import com.jss.osiris.modules.myjss.wordpress.service.MyJssCategoryService;
import com.jss.osiris.modules.myjss.wordpress.service.PostService;
import com.jss.osiris.modules.myjss.wordpress.service.PostViewService;
import com.jss.osiris.modules.myjss.wordpress.service.PublishingDepartmentService;
import com.jss.osiris.modules.myjss.wordpress.service.SerieService;
import com.jss.osiris.modules.myjss.wordpress.service.TagService;
import com.jss.osiris.modules.osiris.crm.model.Comment;
import com.jss.osiris.modules.osiris.crm.service.CommentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.MailService;
import com.jss.osiris.modules.osiris.quotation.model.Announcement;
import com.jss.osiris.modules.osiris.quotation.service.AnnouncementService;

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
	public ResponseEntity<PublishingDepartment> getPublishingDepartmentById(
			@Param("departmentId") Integer departmentId) {
		return new ResponseEntity<PublishingDepartment>(
				publishingDepartmentService.getPublishingDepartment(departmentId),
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

	@GetMapping(inputEntryPoint + "/posts/jss/top")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Post>> getTopJssPosts(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			HttpServletRequest request)
			throws OsirisException {

		detectFlood(request);

		Pageable pageable = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "date"));

		return new ResponseEntity<Page<Post>>(
				postService.getJssCategoryPosts(pageable),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/myjss/top")
	public ResponseEntity<List<Post>> getTopMyJssPosts(@RequestParam Integer page) throws OsirisException {
		return new ResponseEntity<List<Post>>(postService.getMyJssCategoryPosts(page),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/jss/tendency")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<List<Post>> getJssCategoryPostsTendency() throws OsirisException {
		return new ResponseEntity<List<Post>>(postService.getJssCategoryPostTendency(),
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

		Pageable pageableRequest = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "date"));

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

	@GetMapping(inputEntryPoint + "/posts/slug")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Post> getPostBySlug(@RequestParam String slug, HttpServletRequest request)
			throws OsirisException {
		Post post = postService.getPostsBySlug(slug);
		if (post != null && !isCrawler(request))
			postViewService.incrementView(post);
		return new ResponseEntity<Post>(postService.applyPremium(post), HttpStatus.OK);
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

	@GetMapping(inputEntryPoint + "/posts/publishing-department/most-seen")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Post>> getMostSeenPostByPublishingDepartment(
			@RequestParam Integer departmentId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			HttpServletRequest request) {
		detectFlood(request);
		Pageable pageable = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "date"));

		PublishingDepartment publishingDepartment = publishingDepartmentService.getPublishingDepartment(departmentId);

		if (publishingDepartment == null)
			return new ResponseEntity<>(new PageImpl<>(Collections.emptyList()), HttpStatus.OK);

		return new ResponseEntity<Page<Post>>(
				postService.getMostSeenPostByPublishingDepartment(pageable, publishingDepartment),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/publishing-department/all/most-seen")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Post>> getMostSeenPostByIdf(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			HttpServletRequest request) {
		detectFlood(request);
		Pageable pageable = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "date"));

		return new ResponseEntity<Page<Post>>(
				postService.getMostSeenPostByIdf(pageable),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/all/jss-category")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Post>> getAllPostsByJssCategory(
			@RequestParam Integer categoryId,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size, @RequestParam(required = false) String searchText,
			HttpServletRequest request) {

		detectFlood(request);
		Pageable pageableRequest = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "date"));

		JssCategory category = jssCategoryService.getJssCategory(categoryId);

		if (category == null)
			return new ResponseEntity<>(new PageImpl<>(Collections.emptyList()), HttpStatus.OK);

		return new ResponseEntity<Page<Post>>(
				postService.getAllPostsByJssCategory(pageableRequest, category, searchText),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/all/tag")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Post>> getAllPostsByTag(
			@RequestParam String tagSlug,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size, @RequestParam(required = false) String searchText,
			HttpServletRequest request) {

		detectFlood(request);

		Pageable pageableRequest = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "date"));

		Tag tag = tagService.getTagBySlug(tagSlug);

		if (tag == null)
			return new ResponseEntity<>(new PageImpl<>(Collections.emptyList()), HttpStatus.OK);

		return new ResponseEntity<Page<Post>>(
				postService.getAllPostsByTag(pageableRequest, tag, searchText),
				HttpStatus.OK);

	}

	@GetMapping(inputEntryPoint + "/posts/all/author")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Post>> getAllPostsByAuthor(
			@RequestParam String authorSlug,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size, @RequestParam(required = false) String searchText,
			HttpServletRequest request) {

		detectFlood(request);

		Pageable pageableRequest = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "date"));

		Author author = authorService.getAuthorBySlug(authorSlug);

		if (author == null)
			return new ResponseEntity<>(new PageImpl<>(Collections.emptyList()), HttpStatus.OK);

		return new ResponseEntity<Page<Post>>(
				postService.getAllPostsByAuthor(pageableRequest, author, searchText),
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
				postService.getAllPostsBySerie(pageableRequest, serie, searchText),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/all/publishing-department")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Post>> getAllPostsByPublishingDepartment(
			@RequestParam Integer departmentId,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size, @RequestParam(required = false) String searchText,
			HttpServletRequest request) {

		detectFlood(request);

		Pageable pageableRequest = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "date"));

		PublishingDepartment publishingDepartment = publishingDepartmentService.getPublishingDepartment(departmentId);

		if (publishingDepartment == null)
			return new ResponseEntity<>(new PageImpl<>(Collections.emptyList()), HttpStatus.OK);

		return new ResponseEntity<Page<Post>>(
				postService.getAllPostsByPublishingDepartment(pageableRequest, publishingDepartment,
						searchText),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/all/publishing-department/all")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Post>> getAllPostsForIdf(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size, @RequestParam(required = false) String searchText,
			HttpServletRequest request) {

		detectFlood(request);

		Pageable pageableRequest = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "date"));

		return new ResponseEntity<Page<Post>>(
				postService.getAllPostsByIdf(pageableRequest, searchText),
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

	@GetMapping(inputEntryPoint + "/posts/top/tag")
	public ResponseEntity<Page<Post>> getTagBySlug(@RequestParam Integer page,
			@RequestParam Integer tagId) {
		Tag tag = tagService.getTag(tagId);
		if (tag == null)
			return new ResponseEntity<>(new PageImpl<>(Collections.emptyList()), HttpStatus.OK);
		return new ResponseEntity<Page<Post>>(postService.getPostsByTag(page, tag), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/post/next")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Post> getNextPost(@RequestParam Integer idPost) {
		Post post = postService.getPost(idPost);
		if (post == null)
			return new ResponseEntity<Post>(new Post(), HttpStatus.OK);
		return new ResponseEntity<Post>(postService.applyPremium(postService.getNextPost(post)), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/post/previous")
	@JsonView(JacksonViews.MyJssDetailedView.class)
	public ResponseEntity<Post> getPreviousPost(@RequestParam Integer idPost) {
		Post post = postService.getPost(idPost);
		if (post == null)
			return new ResponseEntity<Post>(new Post(), HttpStatus.OK);
		return new ResponseEntity<Post>(postService.applyPremium(postService.getPreviousPost(post)), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/top/department")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<Page<Post>> getTopPostByDepartment(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam Integer departmentId,
			HttpServletRequest request) throws OsirisException {

		Pageable pageable = PageRequest.of(page, ValidationHelper.limitPageSize(size),
				Sort.by(Sort.Direction.DESC, "date"));

		PublishingDepartment department = publishingDepartmentService.getPublishingDepartment(departmentId);
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

	@GetMapping(inputEntryPoint + "/tags/all/tag")
	public ResponseEntity<List<Tag>> getAllTagsByTag(@RequestParam String tagSlug) {

		if (tagSlug == null)
			return new ResponseEntity<List<Tag>>(new ArrayList<Tag>(), HttpStatus.OK);

		Tag tag = tagService.getTagBySlug(tagSlug);

		return new ResponseEntity<List<Tag>>(tagService.getAllTagsByTag(tag), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/tags/all/author")
	public ResponseEntity<List<Tag>> getAllTagsByAuthor(@RequestParam String authorSlug) {

		if (authorSlug == null)
			return new ResponseEntity<List<Tag>>(new ArrayList<Tag>(), HttpStatus.OK);

		Author author = authorService.getAuthorBySlug(authorSlug);

		return new ResponseEntity<List<Tag>>(tagService.getAllTagsByAuthor(author), HttpStatus.OK);
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

	@GetMapping(inputEntryPoint + "/tags/all/publishing-department")
	public ResponseEntity<List<Tag>> getAllTagsByPublishingDepartment(@RequestParam Integer departmentId) {

		if (departmentId == null)
			return new ResponseEntity<List<Tag>>(new ArrayList<Tag>(), HttpStatus.OK);

		PublishingDepartment publishingDepartment = publishingDepartmentService.getPublishingDepartment(departmentId);

		return new ResponseEntity<List<Tag>>(tagService.getAllTagsByPublishingDepartment(publishingDepartment),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/tags/all/publishing-department/all")
	public ResponseEntity<List<Tag>> getAllTagsByIleDeFrance() {

		return new ResponseEntity<List<Tag>>(tagService.getAllTagsByIdf(), HttpStatus.OK);
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

	@GetMapping(inputEntryPoint + "/announcement/top")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<List<Announcement>> getTopAnnouncement(@RequestParam Integer page, HttpServletRequest request)
			throws OsirisException {
		detectFlood(request);
		return new ResponseEntity<List<Announcement>>(announcementService.getTopAnnouncementForWebSite(page),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/announcement/search")
	@JsonView(JacksonViews.MyJssListView.class)
	public ResponseEntity<List<Announcement>> getTopAnnouncementSearch(@RequestParam Integer page,
			@RequestParam String denomination, @RequestParam String siren, @RequestParam String noticeSearch,
			HttpServletRequest request)
			throws OsirisException {
		detectFlood(request);

		if (denomination == null || denomination.trim().length() == 0)
			denomination = "";
		denomination = denomination.trim().toLowerCase();

		if (siren == null || siren.trim().length() == 0)
			siren = "";
		siren = siren.trim().toLowerCase();

		if (noticeSearch == null || noticeSearch.trim().length() == 0)
			noticeSearch = "";
		noticeSearch = noticeSearch.trim().toLowerCase();

		if (denomination.equals("") && siren.equals("") && noticeSearch.equals(""))
			return new ResponseEntity<List<Announcement>>(new ArrayList<Announcement>(), HttpStatus.OK);

		return new ResponseEntity<List<Announcement>>(
				announcementService.getAnnouncementForWebSite(page, denomination, siren, noticeSearch), HttpStatus.OK);
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
			@RequestParam Integer postId)
			throws OsirisException {

		if (comment != null && postId != null && comment.getMail() != null) {

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

	private ResponseEntity<String> detectFlood(HttpServletRequest request) {
		if (lastFloodFlush.isBefore(LocalDateTime.now().minusMinutes(floodFlushDelayMinute)))
			requestCount.clear();
		else
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}

		String ipAddress = request.getRemoteAddr();
		AtomicLong count = requestCount.computeIfAbsent(ipAddress, k -> new AtomicLong());

		if (count.incrementAndGet() > rateLimit) {
			return new ResponseEntity<String>(new HttpHeaders(), HttpStatus.TOO_MANY_REQUESTS);
		}
		return null;
	}

}
