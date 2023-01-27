package com.jss.osiris.modules.quotation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.quotation.model.IProvisionBoardResult;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.ProvisionBoardResult;
import com.jss.osiris.modules.quotation.repository.AnnouncementStatusRepository;
import com.jss.osiris.modules.quotation.repository.BodaccStatusRepository;
import com.jss.osiris.modules.quotation.repository.ProvisionRepository;

@Service
public class ProvisionServiceImpl implements ProvisionService {

    @Autowired
    ProvisionRepository provisionRepository;
    
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
