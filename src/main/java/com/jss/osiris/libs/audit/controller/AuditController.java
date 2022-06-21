package com.jss.osiris.libs.audit.controller;

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

import com.jss.osiris.libs.audit.model.Audit;
import com.jss.osiris.libs.audit.service.AuditService;

@RestController
public class AuditController {

	private static final String inputEntryPoint = "/audit";

	private static final Logger logger = LoggerFactory.getLogger(AuditController.class);

	@Autowired
	AuditService auditService;

	@GetMapping(inputEntryPoint + "/search")
	public ResponseEntity<List<Audit>> getAuditForEntity(@RequestParam String entityType,
			@RequestParam Integer entityId) {
		List<Audit> foundAudit = null;
		try {
			foundAudit = auditService.getAuditForEntity(entityType, entityId);
		} catch (HttpStatusCodeException e) {
			logger.error("HTTP error when fetching searchEntities", e);
			return new ResponseEntity<List<Audit>>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error when fetching Audit", e);
			return new ResponseEntity<List<Audit>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<Audit>>(foundAudit, HttpStatus.OK);
	}
}