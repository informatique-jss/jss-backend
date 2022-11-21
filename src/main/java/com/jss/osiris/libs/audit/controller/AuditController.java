package com.jss.osiris.libs.audit.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jss.osiris.libs.audit.model.Audit;
import com.jss.osiris.libs.audit.service.AuditService;

@RestController
public class AuditController {

	private static final String inputEntryPoint = "/audit";

	@Autowired
	AuditService auditService;

	@GetMapping(inputEntryPoint + "/search")
	public ResponseEntity<List<Audit>> getAuditForEntity(@RequestParam String entityType,
			@RequestParam Integer entityId) {
		return new ResponseEntity<List<Audit>>(auditService.getAuditForEntity(entityType, entityId), HttpStatus.OK);
	}
}