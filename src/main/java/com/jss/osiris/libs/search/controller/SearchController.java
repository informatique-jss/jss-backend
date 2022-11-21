package com.jss.osiris.libs.search.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jss.osiris.libs.search.model.IndexEntity;
import com.jss.osiris.libs.search.service.SearchService;

@RestController
public class SearchController {

	private static final String inputEntryPoint = "/search";

	@Autowired
	SearchService searchService;

	@GetMapping(inputEntryPoint + "/search")
	public ResponseEntity<List<IndexEntity>> searchEntities(@RequestParam String search) {
		if (search != null && search.length() >= 2)
			return new ResponseEntity<List<IndexEntity>>(searchService.searchForEntities(search), HttpStatus.OK);
		return new ResponseEntity<List<IndexEntity>>(HttpStatus.OK);
	}
}