package com.jss.osiris.modules.myjss.wordpress.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.myjss.wordpress.model.Category;
import com.jss.osiris.modules.myjss.wordpress.model.MyJssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.Page;
import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.myjss.wordpress.model.PublishingDepartment;
import com.jss.osiris.modules.myjss.wordpress.model.Serie;
import com.jss.osiris.modules.myjss.wordpress.model.Tag;
import com.jss.osiris.modules.myjss.wordpress.service.CategoryService;
import com.jss.osiris.modules.myjss.wordpress.service.MyJssCategoryService;
import com.jss.osiris.modules.myjss.wordpress.service.PageService;
import com.jss.osiris.modules.myjss.wordpress.service.PostService;
import com.jss.osiris.modules.myjss.wordpress.service.PostViewService;
import com.jss.osiris.modules.myjss.wordpress.service.PublishingDepartmentService;
import com.jss.osiris.modules.myjss.wordpress.service.SerieService;
import com.jss.osiris.modules.myjss.wordpress.service.TagService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class WordpressController {

	private static final String inputEntryPoint = "/wordpress";

	@Autowired
	PublishingDepartmentService publishingDepartmentService;

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

	@GetMapping(inputEntryPoint + "/myjss-categories")
	public ResponseEntity<List<MyJssCategory>> getAvailableMyJssCategories() {
		return new ResponseEntity<List<MyJssCategory>>(myJssCategoryService.getAvailableMyJssCategories(),
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

	@GetMapping(inputEntryPoint + "/pages")
	public ResponseEntity<List<Page>> getAllPages() {
		return new ResponseEntity<List<Page>>(pageService.getAllPages(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/top")
	public ResponseEntity<List<Post>> getTopPosts(@RequestParam Integer page) throws OsirisException {
		return new ResponseEntity<List<Post>>(postService.getPosts(page), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/tendency")
	public ResponseEntity<List<Post>> getPostsTendency() throws OsirisException {
		return new ResponseEntity<List<Post>>(postService.getPostTendency(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/slug")
	public ResponseEntity<Post> getPostBySlung(@RequestParam String slug, HttpServletRequest request)
			throws OsirisException {
		Post post = postService.getPostsBySlug(slug);
		if (post != null && !isCrawler(request))
			postViewService.incrementView(post);
		return new ResponseEntity<Post>(post, HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/serie/slug")
	public ResponseEntity<List<Post>> getPostSerieBySlug(@RequestParam String slug, HttpServletRequest request)
			throws OsirisException {
		Serie serie = serieService.getSerieBySlug(slug);
		if (serie == null)
			return new ResponseEntity<List<Post>>(new ArrayList<Post>(), HttpStatus.OK);
		return new ResponseEntity<List<Post>>(postService.getPostBySerie(serie), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/top/myjss-category")
	public ResponseEntity<List<Post>> getTopPostByCategory(@RequestParam Integer page,
			@RequestParam Integer categoryId) {
		MyJssCategory category = myJssCategoryService.getMyJssCategory(categoryId);
		if (category == null)
			return new ResponseEntity<List<Post>>(new ArrayList<Post>(), HttpStatus.OK);
		return new ResponseEntity<List<Post>>(postService.getPostsByMyJssCategory(page, category), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/top/tag")
	public ResponseEntity<List<Post>> getTagBySlug(@RequestParam Integer page,
			@RequestParam Integer tagId) {
		Tag tag = tagService.getTag(tagId);
		if (tag == null)
			return new ResponseEntity<List<Post>>(new ArrayList<Post>(), HttpStatus.OK);
		return new ResponseEntity<List<Post>>(postService.getPostsByTag(page, tag), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/top/department")
	public ResponseEntity<List<Post>> getTopPostByDepartment(@RequestParam Integer page,
			@RequestParam Integer departmentId) throws OsirisException {
		PublishingDepartment department = publishingDepartmentService.getPublishingDepartment(departmentId);
		if (department == null)
			return new ResponseEntity<List<Post>>(new ArrayList<Post>(), HttpStatus.OK);
		return new ResponseEntity<List<Post>>(postService.getTopPostByDepartment(page, department), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/top/interview")
	public ResponseEntity<List<Post>> getTopPostInterview(@RequestParam Integer page) throws OsirisException {
		return new ResponseEntity<List<Post>>(postService.getPostInterview(page), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/top/podcast")
	public ResponseEntity<List<Post>> getTopPostPodcast(@RequestParam Integer page) throws OsirisException {
		return new ResponseEntity<List<Post>>(postService.getPostPodcast(page), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/tags")
	public ResponseEntity<List<Tag>> getAllTags() {
		return new ResponseEntity<List<Tag>>(tagService.getAvailableTags(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/tag/slug")
	public ResponseEntity<Tag> getTagBySlug(@RequestParam String slug) {
		return new ResponseEntity<Tag>(tagService.getTagBySlug(slug), HttpStatus.OK);
	}

}
