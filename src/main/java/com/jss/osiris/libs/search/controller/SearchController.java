package com.jss.osiris.libs.search.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;

import com.jss.osiris.libs.search.model.IndexEntity;
import com.jss.osiris.libs.search.service.SearchService;

@RestController
public class SearchController {

	private static final String inputEntryPoint = "/search";

	private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

	@Autowired
	SearchService searchService;

	@GetMapping(inputEntryPoint + "/search")
	public ResponseEntity<List<IndexEntity>> searchEntities(@RequestParam String search) {
		List<IndexEntity> foundEntities = null;
		try {
			if (search != null && search.length() >= 2)
				foundEntities = searchService.searchForEntities(search);
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP error when fetching searchEntities", e);
			return new ResponseEntity<List<IndexEntity>>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error when fetching searchEntities", e);
			return new ResponseEntity<List<IndexEntity>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<IndexEntity>>(foundEntities, HttpStatus.OK);
	}
}