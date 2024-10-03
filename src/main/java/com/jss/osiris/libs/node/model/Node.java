package com.jss.osiris.libs.node.model;

import java.time.LocalDateTime;

import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
public class Node implements IId {

    @Id
    @SequenceGenerator(name = "node_sequence", sequenceName = "node_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "node_sequence")
    private Integer id;
    private String hostname;
    private Integer batchNodePriority;
    private LocalDateTime lastAliveDatetime;
    private Float freeSpace;
    private Float totalSpace;
    private Float freeMemory;
    private Float totalMemory;
    private Float cpuLoad;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public LocalDateTime getLastAliveDatetime() {
        return lastAliveDatetime;
    }

    public void setLastAliveDatetime(LocalDateTime lastAliveDatetime) {
        this.lastAliveDatetime = lastAliveDatetime;
    }

    public Integer getBatchNodePriority() {
        return batchNodePriority;
    }

    public void setBatchNodePriority(Integer batchNodePriority) {
        this.batchNodePriority = batchNodePriority;
    }

    public Float getFreeSpace() {
        return freeSpace;
    }

    public void setFreeSpace(Float freeSpace) {
        this.freeSpace = freeSpace;
    }

    public Float getTotalSpace() {
        return totalSpace;
    }

    public void setTotalSpace(Float totalSpace) {
        this.totalSpace = totalSpace;
    }

    public Float getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(Float freeMemory) {
        this.freeMemory = freeMemory;
    }

    public Float getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(Float totalMemory) {
        this.totalMemory = totalMemory;
    }

    public Float getCpuLoad() {
        return cpuLoad;
    }

    public void setCpuLoad(Float cpuLoad) {
        this.cpuLoad = cpuLoad;
    }

}
