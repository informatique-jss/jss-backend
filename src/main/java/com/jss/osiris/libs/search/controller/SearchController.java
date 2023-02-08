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

	@GetMapping(inputEntryPoint + "/search/type")
	public ResponseEntity<List<IndexEntity>> searchEntitiesAndType(@RequestParam String search,
			@RequestParam String entityType) {
		if (search != null && search.length() >= 2)
			return new ResponseEntity<List<IndexEntity>>(searchService.searchForEntities(search, entityType),
					HttpStatus.OK);
		return new ResponseEntity<List<IndexEntity>>(HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/individual/search")
	public ResponseEntity<List<IndexEntity>> getIndividualTiersByKeyword(@RequestParam String searchedValue) {
		return new ResponseEntity<List<IndexEntity>>(searchService.getIndividualTiersByKeyword(searchedValue),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/responsable/search")
	public ResponseEntity<List<IndexEntity>> getActifResponsableByKeyword(@RequestParam String searchedValue,
			@RequestParam Boolean onlyActive) {
		return new ResponseEntity<List<IndexEntity>>(
				searchService.getActifResponsableByKeyword(searchedValue, onlyActive),
				HttpStatus.OK);
	}

	@GetMapping(inputEntryPoint + "/customer/order/search")
	public ResponseEntity<List<IndexEntity>> getCustomerOrdersByKeyword(@RequestParam String searchedValue) {
		return new ResponseEntity<List<IndexEntity>>(
				searchService.getCustomerOrdersByKeyword(searchedValue),
				HttpStatus.OK);
	}
}