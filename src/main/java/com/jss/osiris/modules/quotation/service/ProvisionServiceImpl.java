package com.jss.osiris.modules.quotation.service;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.quotation.model.AnnouncementStatus;
import com.jss.osiris.modules.quotation.model.BodaccStatus;
import com.jss.osiris.modules.quotation.model.IProvisionBoardResult;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.ProvisionBoardResult;
import com.jss.osiris.modules.quotation.model.ProvisionStatus;
import com.jss.osiris.modules.quotation.repository.AnnouncementStatusRepository;
import com.jss.osiris.modules.quotation.repository.BodaccStatusRepository;
import com.jss.osiris.modules.quotation.repository.ProvisionRepository;

@Service
public class ProvisionServiceImpl implements ProvisionService {

    @Autowired
    ProvisionRepository provisionRepository;

    @Autowired
    SearchBoardStatus searchBoardStatus;
    
    @Autowired
    BodaccStatusRepository bodaccStatusRepository;

    @Autowired
    AnnouncementStatusRepository announcementStatusRepository;


    @Override
    public Provision getProvision(Integer id) {
        Optional<Provision> provision = provisionRepository.findById(id);
        if (provision.isPresent())
            return provision.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAssignedToForProvision(Provision provision, Employee employee) {
        provision.setAssignedTo(employee);
        provisionRepository.save(provision);
    }

    /**
     * Set status in workflow order (beginning with new) group by same code
     * @return
     */
    public List<List<ProvisionStatus>> getBoardAnnouncementStatus() {

        ProvisionStatus first = null;

        final String DEB_STATUS_ANNOUNCEMENT = "ANNOUNCEMENT";
        final String DEB_STATUS_BODACC = "BODACC";


        // ANNOUNCEMENT
        Iterable<AnnouncementStatus> announcementStatusList =announcementStatusRepository.findAll();
        List<ProvisionStatus> announcementStatusListOrigin = new ArrayList<>();
        announcementStatusList.forEach(a -> announcementStatusListOrigin.add(a));

        first = searchBoardStatus.searchFirstStatus(announcementStatusListOrigin, DEB_STATUS_ANNOUNCEMENT);

        searchBoardStatus.searchStatusItem(first, announcementStatusListOrigin, DEB_STATUS_ANNOUNCEMENT);


        // BODACC
        Iterable<BodaccStatus> bodaccStatusList = bodaccStatusRepository.findAll();
        List<ProvisionStatus> bodaccStatusListOrigin = new ArrayList<>();
        bodaccStatusList.forEach(b -> bodaccStatusListOrigin.add(b));

        first = searchBoardStatus.searchFirstStatus(bodaccStatusListOrigin, DEB_STATUS_BODACC);

        searchBoardStatus.searchStatusItem(first, bodaccStatusListOrigin, DEB_STATUS_BODACC);


        return searchBoardStatus.getStatusList();
    }


    public List<List<ProvisionStatus>> getBoardFormaliteStatus() {

        return null;
    }

    /**
     * Data to display in announcement + bodacc board
     */
    public List<ProvisionBoardResult> getBoardALs(List<Integer> employees) {
        List<ProvisionBoardResult> result = new ArrayList<ProvisionBoardResult>();

        List<IProvisionBoardResult> resultRepository = provisionRepository.getBoardALs(employees);
        
        resultRepository.forEach((item) -> {
            if (item.getStatus() != null) {
                ProvisionBoardResult current = null;
                current = new ProvisionBoardResult(item.getEmployee(), item.getNbProvision(), item.getStatus(), 
                                                    item.getPriority1(), item.getPriority2(), item.getPriority3());
                result.add(current);
            }
        });
        return result;
    }


    public List<ProvisionBoardResult> getBoardFormalite(List<Integer> employees) {
        List<ProvisionBoardResult> result = new ArrayList<ProvisionBoardResult>();

        List<IProvisionBoardResult> resultRepository = provisionRepository.getBoardFormalite(employees);
        
        resultRepository.forEach((item) -> {
            if (item.getStatus() != null) {
                ProvisionBoardResult current = null;
                current = new ProvisionBoardResult(item.getEmployee(), item.getNbProvision(), item.getStatus(), 
                                                    item.getPriority1(), item.getPriority2(), item.getPriority3());
                result.add(current);
            }
        });
        return result;
    }


}
