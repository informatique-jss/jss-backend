package com.jss.osiris.libs.node.service;

import java.util.List;

import org.springframework.web.client.RestClientException;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.node.model.Node;

public interface NodeService {
    public Node addOrUpdateNode(Node node);

    public Node getNode(Integer id);

    public List<Node> getAllNodes();

    public void performGc(Node node) throws RestClientException, OsirisException;

    public void stopNode(Node node) throws RestClientException, OsirisException;

    public void restartNode(Node node) throws RestClientException, OsirisException;

    public void updateNodeStatus() throws OsirisException;

    public Node getCurrentNode() throws OsirisException;

    public Node getCurrentNodeCached() throws OsirisException;

    public boolean shouldIBatch() throws OsirisException;

}
