package com.jss.osiris.modules.myjss.miscellaneous.controller;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.myjss.miscellaneous.model.Sitemap;
import com.jss.osiris.modules.osiris.miscellaneous.service.StorageFileService;

@RestController
public class MyJssMiscellaneousController {

	// private static final String inputEntryPoint = "/myjss/miscellaneous";

	@Autowired
	StorageFileService storageFileService;

	@Value("${upload.file.directory}")
	private String uploadFolder;

	@GetMapping(value = "/{filename:.+\\.xml}", produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<Resource> getSitemap(@PathVariable String filename)
			throws OsirisValidationException, OsirisException {
		File file = new File(uploadFolder.trim() + File.separator + Sitemap.siteMapFolder, filename);

		if (!file.exists()) {
			return ResponseEntity.badRequest().build();
		} else {
			Resource resource = new FileSystemResource(file);
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
					.contentType(MediaType.APPLICATION_XML)
					.body(resource);
		}
	}
}
