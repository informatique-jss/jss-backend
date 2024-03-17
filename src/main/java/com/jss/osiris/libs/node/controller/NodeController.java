package com.jss.osiris.libs.node.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.node.model.Node;
import com.jss.osiris.libs.node.service.NodeService;

@RestController
public class NodeController {

	private static final String inputEntryPoint = "/node";

	@Autowired
	NodeService nodeService;

	@PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
	@GetMapping(inputEntryPoint + "/nodes")
	public ResponseEntity<List<Node>> getNodes() {
		return new ResponseEntity<List<Node>>(nodeService.getAllNodes(), HttpStatus.OK);
	}

	@PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
	@GetMapping(inputEntryPoint + "/node/gc")
	public ResponseEntity<Boolean> performGc(@RequestParam Integer nodeId)
			throws OsirisValidationException, RestClientException, OsirisException {
		Node node = nodeService.getNode(nodeId);
		if (node == null)
			throw new OsirisValidationException("nodeId");

		nodeService.performGc(node);
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
	@GetMapping(inputEntryPoint + "/node/stop")
	public ResponseEntity<Boolean> stopNode(@RequestParam Integer nodeId)
			throws OsirisValidationException, RestClientException, OsirisException {
		Node node = nodeService.getNode(nodeId);
		if (node == null)
			throw new OsirisValidationException("nodeId");

		nodeService.stopNode(node);
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
	@GetMapping(inputEntryPoint + "/node/restart")
	public ResponseEntity<Boolean> restartNode(@RequestParam Integer nodeId)
			throws OsirisValidationException, RestClientException, OsirisException {
		Node node = nodeService.getNode(nodeId);
		if (node == null)
			throw new OsirisValidationException("nodeId");

		nodeService.restartNode(node);
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}
}