package com.jss.osiris.modules.myjss.wordpress.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.libs.search.model.IndexEntity;
import com.jss.osiris.libs.search.service.SearchService;
import com.jss.osiris.modules.myjss.wordpress.model.Author;
import com.jss.osiris.modules.myjss.wordpress.model.Category;
import com.jss.osiris.modules.myjss.wordpress.model.JssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.MyJssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.Page;
import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.myjss.wordpress.model.PublishingDepartment;
import com.jss.osiris.modules.myjss.wordpress.model.Serie;
import com.jss.osiris.modules.myjss.wordpress.model.Tag;
import com.jss.osiris.modules.myjss.wordpress.service.AuthorService;
import com.jss.osiris.modules.myjss.wordpress.service.CategoryService;
import com.jss.osiris.modules.myjss.wordpress.service.JssCategoryService;
import com.jss.osiris.modules.myjss.wordpress.service.MyJssCategoryService;
import com.jss.osiris.modules.myjss.wordpress.service.PageService;
import com.jss.osiris.modules.myjss.wordpress.service.PostService;
import com.jss.osiris.modules.myjss.wordpress.service.PostViewService;
import com.jss.osiris.modules.myjss.wordpress.service.PublishingDepartmentService;
import com.jss.osiris.modules.myjss.wordpress.service.SerieService;
import com.jss.osiris.modules.myjss.wordpress.service.TagService;
import com.jss.osiris.modules.osiris.quotation.model.Announcement;
import com.jss.osiris.modules.osiris.quotation.service.AnnouncementService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class WordpressController {

	private static final String inputEntryPoint = "/wordpress";

	private final ConcurrentHashMap<String, AtomicLong> requestCount = new ConcurrentHashMap<>();
	private final long rateLimit = 10;
	private LocalDateTime lastFloodFlush = LocalDateTime.now();
	private int floodFlushDelayMinute = 1;

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
	PageService pageService;

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

	@GetMapping(inputEntryPoint + "/jss-categories")
	public ResponseEntity<List<JssCategory>> getAvailableJssCategories() {
		return new ResponseEntity<List<JssCategory>>(jssCategoryService.getAvailableJssCategories(),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/categories")
	public ResponseEntity<List<Category>> getAvailableCategories() {
		return new ResponseEntity<List<Category>>(categoryService.getAvailableCategories(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/series")
	public ResponseEntity<List<Serie>> getAvailableSeries() {
		return new ResponseEntity<List<Serie>>(serieService.getAvailableSeries(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/serie/slug")
	public ResponseEntity<Serie> getSerieBySlug(@RequestParam String slug) {
		return new ResponseEntity<Serie>(serieService.getSerieBySlug(slug), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/author/slug")
	public ResponseEntity<Author> getAuthorBySlug(@RequestParam String slug) {
		return new ResponseEntity<Author>(authorService.getAuthorBySlug(slug), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/pages")
	public ResponseEntity<List<Page>> getAllPages() {
		return new ResponseEntity<List<Page>>(pageService.getAllPages(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/top")
	public ResponseEntity<List<Post>> getTopPosts(@RequestParam Integer page) throws OsirisException {
		return new ResponseEntity<List<Post>>(postService.applyPremium(postService.getPosts(page)), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/tendency")
	public ResponseEntity<List<Post>> getPostsTendency() throws OsirisException {
		return new ResponseEntity<List<Post>>(postService.applyPremium(postService.getPostTendency()), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/slug")
	public ResponseEntity<Post> getPostBySlung(@RequestParam String slug, HttpServletRequest request)
			throws OsirisException {
		Post post = postService.getPostsBySlug(slug);
		if (post != null && !isCrawler(request))
			postViewService.incrementView(post);
		return new ResponseEntity<Post>(postService.applyPremium(post), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/serie/slug")
	public ResponseEntity<List<Post>> getPostSerieBySlug(@RequestParam String slug, HttpServletRequest request)
			throws OsirisException {
		Serie serie = serieService.getSerieBySlug(slug);
		if (serie == null)
			return new ResponseEntity<List<Post>>(new ArrayList<Post>(), HttpStatus.OK);
		return new ResponseEntity<List<Post>>(postService.applyPremium(postService.getPostBySerie(serie)),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/top/jss-category")
	public ResponseEntity<List<Post>> getTopPostByCategory(@RequestParam Integer page,
			@RequestParam Integer categoryId) {
		JssCategory category = jssCategoryService.getJssCategory(categoryId);
		if (category == null)
			return new ResponseEntity<List<Post>>(new ArrayList<Post>(), HttpStatus.OK);
		return new ResponseEntity<List<Post>>(
				postService.applyPremium(postService.getPostsByJssCategory(page, category)), HttpStatus.OK);
	}

	@GetMapping("myjss" + inputEntryPoint + "/myjss-categories")
	public ResponseEntity<List<MyJssCategory>> getAvailableMyJssCategories() {
		return new ResponseEntity<List<MyJssCategory>>(
				myJssCategoryService.getAvailableMyJssCategories(), HttpStatus.OK);
	}

	@GetMapping("myjss" + inputEntryPoint + "/posts/first-myjss-category")
	public ResponseEntity<List<Post>> getFirstPostsByMyJssCategories(@RequestParam(required = false) String searchText,
			@RequestParam(required = false) Integer myJssCategoryId, HttpServletRequest request) {
		detectFlood(request);
		MyJssCategory myJssCategory = null;
		if (myJssCategoryId != null)
			myJssCategory = myJssCategoryService.getMyJssCategory(myJssCategoryId);

		return new ResponseEntity<List<Post>>(postService.getFirstPostsByMyJssCategories(myJssCategory),
				HttpStatus.OK);
	}

	@GetMapping("myjss" + inputEntryPoint + "/search/myjss-category")
	public ResponseEntity<List<Post>> searchPostsByMyJssCategory(@RequestParam String searchText,
			@RequestParam(required = false) Integer myJssCategoryId,
			HttpServletRequest request) {
		detectFlood(request);

		if (searchText == null || searchText.trim().length() == 0)
			searchText = "";
		searchText = searchText.trim().toLowerCase();

		MyJssCategory myJssCategory = null;
		if (myJssCategoryId != null)
			myJssCategory = myJssCategoryService.getMyJssCategory(myJssCategoryId);

		if (searchText.equals("") && myJssCategory == null)
			return new ResponseEntity<List<Post>>(new ArrayList<Post>(), HttpStatus.OK);

		return new ResponseEntity<List<Post>>(
				postService.searchPostsByMyJssCategory(searchText, myJssCategory), HttpStatus.OK);
	}

	@GetMapping("myjss" + inputEntryPoint + "/posts/myjss-category")
	public ResponseEntity<List<Post>> getPostsByMyJssCategory(@RequestParam Integer myJssCategoryId,
			@RequestParam(required = false) String searchText,
			HttpServletRequest request) {
		detectFlood(request);

		MyJssCategory myJssCategory = null;
		if (myJssCategoryId != null)
			myJssCategory = myJssCategoryService.getMyJssCategory(myJssCategoryId);

		if (myJssCategory == null)
			return new ResponseEntity<List<Post>>(new ArrayList<Post>(), HttpStatus.OK);

		return new ResponseEntity<List<Post>>(
				postService.searchPostsByMyJssCategory(searchText, myJssCategory), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/top/tag")
	public ResponseEntity<List<Post>> getTagBySlug(@RequestParam Integer page,
			@RequestParam Integer tagId) {
		Tag tag = tagService.getTag(tagId);
		if (tag == null)
			return new ResponseEntity<List<Post>>(new ArrayList<Post>(), HttpStatus.OK);
		return new ResponseEntity<List<Post>>(postService.applyPremium(postService.getPostsByTag(page, tag)),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/post/next")
	public ResponseEntity<Post> getNextPost(@RequestParam Integer idPost) {
		Post post = postService.getPost(idPost);
		if (post == null)
			return new ResponseEntity<Post>(new Post(), HttpStatus.OK);
		return new ResponseEntity<Post>(postService.getNextPost(post), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/post/previous")
	public ResponseEntity<Post> getPreviousPost(@RequestParam Integer idPost) {
		Post post = postService.getPost(idPost);
		if (post == null)
			return new ResponseEntity<Post>(new Post(), HttpStatus.OK);
		return new ResponseEntity<Post>(postService.getPreviousPost(post), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/top/author")
	public ResponseEntity<List<Post>> getTopPostByAuthor(@RequestParam Integer page, @RequestParam Integer authorId) {
		Author author = authorService.getAuthor(authorId);
		if (author == null)
			return new ResponseEntity<List<Post>>(new ArrayList<Post>(), HttpStatus.OK);
		return new ResponseEntity<List<Post>>(postService.applyPremium(postService.getPostsByAuthor(page, author)),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/top/department")
	public ResponseEntity<List<Post>> getTopPostByDepartment(@RequestParam Integer page,
			@RequestParam Integer departmentId) throws OsirisException {
		PublishingDepartment department = publishingDepartmentService.getPublishingDepartment(departmentId);
		if (department == null)
			return new ResponseEntity<List<Post>>(new ArrayList<Post>(), HttpStatus.OK);
		return new ResponseEntity<List<Post>>(
				postService.applyPremium(postService.getTopPostByDepartment(page, department)), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/top/interview")
	public ResponseEntity<List<Post>> getTopPostInterview(@RequestParam Integer page) throws OsirisException {
		return new ResponseEntity<List<Post>>(postService.applyPremium(postService.getPostInterview(page)),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/top/podcast")
	public ResponseEntity<List<Post>> getTopPostPodcast(@RequestParam Integer page) throws OsirisException {
		return new ResponseEntity<List<Post>>(postService.applyPremium(postService.getPostPodcast(page)),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/tags")
	public ResponseEntity<List<Tag>> getAllTags() {
		return new ResponseEntity<List<Tag>>(tagService.getAvailableTags(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/tag/slug")
	public ResponseEntity<Tag> getTagBySlug(@RequestParam String slug) {
		return new ResponseEntity<Tag>(tagService.getTagBySlug(slug), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/search/post")
	@JsonView(JacksonViews.MyJssView.class)
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
	@JsonView(JacksonViews.MyJssView.class)
	public ResponseEntity<List<Announcement>> getTopAnnouncement(@RequestParam Integer page, HttpServletRequest request)
			throws OsirisException {
		detectFlood(request);
		return new ResponseEntity<List<Announcement>>(announcementService.getTopAnnouncementForWebSite(page),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/announcement/search")
	@JsonView(JacksonViews.MyJssView.class)
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
	@JsonView(JacksonViews.MyJssView.class)
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
