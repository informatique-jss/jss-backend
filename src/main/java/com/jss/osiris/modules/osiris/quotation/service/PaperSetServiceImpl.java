package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderComment;
import com.jss.osiris.modules.osiris.quotation.model.IPaperSetResult;
import com.jss.osiris.modules.osiris.quotation.model.PaperSet;
import com.jss.osiris.modules.osiris.quotation.repository.PaperSetRepository;

@Service
public class PaperSetServiceImpl implements PaperSetService {

    @Autowired
    PaperSetRepository paperSetRepository;

    @Autowired
    CustomerOrderCommentService customerOrderCommentService;

    @Autowired
    ConstantService constantService;

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

    public PaperSet cancelPaperSet(PaperSet paperSet) throws OsirisException {
        paperSet = getPaperSet(paperSet.getId());
        paperSet.setIsCancelled(true);
        CustomerOrderComment customerOrderComment = customerOrderCommentService.createCustomerOrderComment(
                paperSet.getCustomerOrder(),
                "L'action " + paperSet.getPaperSetType().getLabel() + " n°" + paperSet.getId()
                        + " a été annulée (emplacement n°" + paperSet.getLocationNumber() + "). "
                        + paperSet.getValidationComment(),
                false, false);

        customerOrderCommentService.tagActiveDirectoryGroupOnCustomerOrderComment(customerOrderComment,
                constantService.getActiveDirectoryGroupFormalites());
        return addOrUpdatePaperSet(paperSet);
    }

    public PaperSet validatePaperSet(PaperSet paperSet) throws OsirisException {
        paperSet = getPaperSet(paperSet.getId());
        paperSet.setIsValidated(true);
        CustomerOrderComment customerOrderComment = customerOrderCommentService.createCustomerOrderComment(
                paperSet.getCustomerOrder(),
                "L'action " + paperSet.getPaperSetType().getLabel() + " n°" + paperSet.getId()
                        + " a été effectuée (emplacement n°" + paperSet.getLocationNumber() + "). "
                        + paperSet.getValidationComment(),
                false, false);

        customerOrderCommentService.tagActiveDirectoryGroupOnCustomerOrderComment(customerOrderComment,
                constantService.getActiveDirectoryGroupFormalites());
        return addOrUpdatePaperSet(paperSet);
    }
}
