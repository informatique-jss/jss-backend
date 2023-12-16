package com.jss.osiris.libs.node.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.node.model.Node;

public interface NodeService {
    public Node addOrUpdateNode(Node node);

    public List<Node> getAllNodes();

    public void updateNodeStatus() throws OsirisException;

    public Node getCurrentNode() throws OsirisException;

}
