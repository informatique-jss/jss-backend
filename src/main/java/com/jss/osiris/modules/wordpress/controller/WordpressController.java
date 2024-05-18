package com.jss.osiris.modules.wordpress.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jss.osiris.modules.wordpress.model.Category;
import com.jss.osiris.modules.wordpress.model.MyJssCategory;
import com.jss.osiris.modules.wordpress.model.Page;
import com.jss.osiris.modules.wordpress.model.Post;
import com.jss.osiris.modules.wordpress.model.PublishingDepartment;
import com.jss.osiris.modules.wordpress.model.Tag;
import com.jss.osiris.modules.wordpress.service.CategoryService;
import com.jss.osiris.modules.wordpress.service.MyJssCategoryService;
import com.jss.osiris.modules.wordpress.service.PageService;
import com.jss.osiris.modules.wordpress.service.PostService;
import com.jss.osiris.modules.wordpress.service.PublishingDepartmentService;
import com.jss.osiris.modules.wordpress.service.TagService;

@RestController
public class WordpressController {

	private static final String inputEntryPoint = "/my-jss/wordpress";

	@Autowired
	PublishingDepartmentService publishingDepartmentService;

	@Autowired
	MyJssCategoryService myJssCategoryService;

	@Autowired
	CategoryService categoryService;

	@Autowired
	PageService pageService;

	@Autowired
	TagService tagService;

	@Autowired
	PostService postService;

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

	@GetMapping(inputEntryPoint + "/pages")
	public ResponseEntity<List<Page>> getAllPages() {
		return new ResponseEntity<List<Page>>(pageService.getAllPages(), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/top")
	public ResponseEntity<List<Post>> getTopPosts(@RequestParam Integer page) {
		return new ResponseEntity<List<Post>>(postService.getPosts(page), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/top/myjss-category")
	public ResponseEntity<List<Post>> getTopPostByCategory(@RequestParam Integer page,
			@RequestParam Integer categoryId) {
		return new ResponseEntity<List<Post>>(postService.getPostsByMyJssCategory(page, categoryId), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/top/interview")
	public ResponseEntity<List<Post>> getTopPostInterview(@RequestParam Integer page) {
		return new ResponseEntity<List<Post>>(postService.getPostInterview(page), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/posts/top/podcast")
	public ResponseEntity<List<Post>> getTopPostPodcast(@RequestParam Integer page) {
		return new ResponseEntity<List<Post>>(postService.getPostPodcast(page), HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/tags")
	public ResponseEntity<List<Tag>> getAllTags() {
		return new ResponseEntity<List<Tag>>(tagService.getAvailableTags(), HttpStatus.OK);
	}

}
