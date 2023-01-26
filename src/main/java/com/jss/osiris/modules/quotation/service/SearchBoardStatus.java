package com.jss.osiris.modules.quotation.service;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.jss.osiris.modules.quotation.model.ProvisionStatus;


@Component
public class SearchBoardStatus {
    private final String STATUS_DEB_WORKFLOW = "NEW";

    private List<List<ProvisionStatus>> statusList = new ArrayList<List<ProvisionStatus>>();
    private Hashtable<String, Integer> assoStatusIndex = new Hashtable<String, Integer>();

    public String getSTATUS_DEB_WORKFLOW() {
        return STATUS_DEB_WORKFLOW;
    }

    public List<List<ProvisionStatus>> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<List<ProvisionStatus>> statusList) {
        this.statusList = statusList;
    }


    /**
     * Rempli statusList par recurence
     * @param current
     * @param list
     * @param debCodeStatus
     */
    public ProvisionStatus searchFirstStatus(List<ProvisionStatus> list, String debCodeStatus) {
        String codeToSearch = debCodeStatus+"_"+STATUS_DEB_WORKFLOW;
    
        Optional<ProvisionStatus> current = list.stream().filter((ProvisionStatus p) -> p.getCode().equalsIgnoreCase(codeToSearch)).findFirst();

        return current.get();
    }

    /**
     * Rempli statusList par recurence
     * @param current
     * @param list
     * @param debCodeStatus
     */
    public void searchStatusItem(ProvisionStatus current, List<ProvisionStatus> list, String debCodeStatus) {

        List<ProvisionStatus> item = null;

        // If current is not in list, the workflow ended for this branch
        if (!list.removeIf( i -> i.getCode().equalsIgnoreCase(current.getCode()) )) 
            return;

        // Add current in statusList
        String codeToSearch = current.getCode().trim().substring(debCodeStatus.length()+1);
        Integer index = assoStatusIndex.get(codeToSearch);

        if (index == null) {
            item = new ArrayList<ProvisionStatus>();
            item.add(current);
            statusList.add(item);
            assoStatusIndex.put(codeToSearch, statusList.size()-1);
        }
        else {
            item = statusList.get(index.intValue());
            item.add(current);
        }

        // searchStatusItem for successors
        List<ProvisionStatus> successors = current.getSuccessors();
        for (ProvisionStatus succ : successors) {
            searchStatusItem(succ, list, debCodeStatus);
        }

    }

}

