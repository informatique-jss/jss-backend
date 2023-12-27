package com.jss.osiris.libs.node.repository;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.libs.node.model.Node;

public interface NodeRepository extends CrudRepository<Node, Integer> {

    Node findByHostname(String hostname);

}