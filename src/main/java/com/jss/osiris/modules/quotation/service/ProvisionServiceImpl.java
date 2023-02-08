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
import com.jss.osiris.modules.quotation.model.ProvisionBoardDisplayedResult;
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
    public List<ProvisionBoardDisplayedResult> getBoardALs(List<Integer> employees) {
 
        List<IProvisionBoardResult> resultRepository = provisionRepository.getBoardALs(employees);

        return updateProvisionBoardDisplayedResult(resultRepository);
    }

    /**
     * Data to display in simple formalite + formalite + domiciliation board
     */
 
    public List<ProvisionBoardDisplayedResult> getBoardFormalite(List<Integer> employees) {

        List<IProvisionBoardResult> resultRepository = provisionRepository.getBoardFormalite(employees);

        return updateProvisionBoardDisplayedResult(resultRepository);
    }

    private List<ProvisionBoardDisplayedResult> updateProvisionBoardDisplayedResult(List<IProvisionBoardResult> resultRepository) {
        List<ProvisionBoardDisplayedResult> result = new ArrayList<ProvisionBoardDisplayedResult>();
        
        resultRepository.forEach((item) -> {
            if (item.getStatus() != null && item.getEmployee() != null && item.getNbProvision() != null) {
                ProvisionBoardDisplayedResult ourResult = result.stream()
                    .filter(oneResult -> item.getEmployee().equals(oneResult.getEmployee()))
                    .findAny()
                    .orElse(null);

                if (ourResult == null) {
                    ourResult = new ProvisionBoardDisplayedResult(item.getEmployee());
                    result.add(ourResult);
                }
                ourResult.updateProvisionBoardDisplayedResult(item.getStatus(), item.getNbProvision());
            }
        });

        return result;
    }


}
