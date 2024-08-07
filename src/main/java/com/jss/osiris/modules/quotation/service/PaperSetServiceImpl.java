package com.jss.osiris.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.quotation.model.IPaperSetResult;
import com.jss.osiris.modules.quotation.model.PaperSet;
import com.jss.osiris.modules.quotation.repository.PaperSetRepository;

@Service
public class PaperSetServiceImpl implements PaperSetService {

    @Autowired
    PaperSetRepository paperSetRepository;

    @Autowired
    CustomerOrderCommentService customerOrderCommentService;

    @Override
    public List<PaperSet> getPaperSets() {
        return IterableUtils.toList(paperSetRepository.findAll());
    }

    @Override
    public PaperSet getPaperSet(Integer id) {
        Optional<PaperSet> paperSet = paperSetRepository.findById(id);
        if (paperSet.isPresent())
            return paperSet.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaperSet addOrUpdatePaperSet(PaperSet paperSet) {
        if (paperSet.getLocationNumber() == null) {
            // Allocate new location number
            List<PaperSet> paperSets = paperSetRepository.findAllByOrderByLocationNumberAsc();
            Integer location = 1;
            if (paperSet != null) {
                for (PaperSet currentPaperSet : paperSets) {
                    if (currentPaperSet.getLocationNumber().equals(location)) {
                        location++;
                    } else {
                        break;
                    }
                }
            }
            paperSet.setLocationNumber(location);
            if (paperSet.getIsCancelled() == null)
                paperSet.setIsCancelled(false);
            if (paperSet.getIsValidated() == null)
                paperSet.setIsValidated(false);
        }
        return paperSetRepository.save(paperSet);
    }

    public List<IPaperSetResult> searchPaperSets(String textSearch, Boolean isDisplayValidated,
            Boolean isDisplayCancelled) {
        return paperSetRepository.findPaperSets(textSearch, isDisplayValidated, isDisplayCancelled);
    }

    public PaperSet cancelPaperSet(PaperSet paperSet) {
        paperSet = getPaperSet(paperSet.getId());
        paperSet.setIsCancelled(true);
        customerOrderCommentService.createCustomerOrderComment(paperSet.getCustomerOrder(),
                "L'action " + paperSet.getPaperSetType().getLabel() + " n°" + paperSet.getId()
                        + " a été annulée (emplacement n°" + paperSet.getLocationNumber() + ")");
        return addOrUpdatePaperSet(paperSet);
    }

    public PaperSet validatePaperSet(PaperSet paperSet) {
        paperSet = getPaperSet(paperSet.getId());
        paperSet.setIsValidated(true);
        customerOrderCommentService.createCustomerOrderComment(paperSet.getCustomerOrder(),
                "L'action " + paperSet.getPaperSetType().getLabel() + " n°" + paperSet.getId()
                        + " a été effectuée (emplacement n°" + paperSet.getLocationNumber() + ")");
        return addOrUpdatePaperSet(paperSet);
    }
}
