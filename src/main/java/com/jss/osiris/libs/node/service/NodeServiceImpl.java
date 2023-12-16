package com.jss.osiris.libs.node.service;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.node.model.Node;
import com.jss.osiris.libs.node.repository.NodeRepository;

@Service
public class NodeServiceImpl implements NodeService {

    @Autowired
    NodeRepository nodeRepository;

    @Value("${schedulling.node.priority}")
    private Integer nodeSchedullingPriority;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Node addOrUpdateNode(Node node) {
        return nodeRepository.save(node);
    }

    @Override
    public List<Node> getAllNodes() {
        return IterableUtils.toList(nodeRepository.findAll());
    }

    @Override
    public Node getCurrentNode() throws OsirisException {
        return nodeRepository.findByHostname(getHostname());
    }

    @Override
    public void updateNodeStatus() throws OsirisException {

        Node node = nodeRepository.findByHostname(getHostname());

        if (node == null) {
            node = new Node();
            node.setHostname(getHostname());
        }
        node.setLastAliveDatetime(LocalDateTime.now());
        node.setBatchNodePriority(nodeSchedullingPriority);

        File root;
        if (SystemUtils.IS_OS_UNIX)
            root = new File("/");
        else
            root = new File("C:");
        node.setTotalSpace(root.getTotalSpace() / 1073741824f);
        node.setFreeSpace(root.getFreeSpace() / 1073741824f);

        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        node.setTotalMemory(memoryMXBean.getHeapMemoryUsage().getMax() * 1.0f);
        node.setFreeMemory(
                (memoryMXBean.getHeapMemoryUsage().getMax() - memoryMXBean.getHeapMemoryUsage().getUsed()) * 1.0f);

        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
            AttributeList list = mbs.getAttributes(name, new String[] { "ProcessCpuLoad" });

            node.setCpuLoad(Optional.ofNullable(list)
                    .map(l -> l.isEmpty() ? null : l)
                    .map(List::iterator)
                    .map(Iterator::next)
                    .map(Attribute.class::cast)
                    .map(Attribute::getValue)
                    .map(Double.class::cast)
                    .orElse(null).floatValue());
        } catch (Exception e) {
        }

        addOrUpdateNode(node);
    }

    private String getHostname() throws OsirisException {
        String hostname;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new OsirisException(e, "");
        }
        return hostname;
    }

}
