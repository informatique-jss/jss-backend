package com.jss.osiris.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.search.service.IndexEntityService;
import com.jss.osiris.modules.invoicing.model.DebourSearch;
import com.jss.osiris.modules.invoicing.model.DebourSearchResult;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.quotation.model.Debour;
import com.jss.osiris.modules.quotation.repository.DebourRepository;

@Service
public class DebourServiceImpl implements DebourService {

    @Autowired
    DebourRepository debourRepository;

    @Autowired
    IndexEntityService indexEntityService;

    @Autowired
    ConstantService constantService;

    @Override
    public Debour getDebour(Integer id) {
        Optional<Debour> debour = debourRepository.findById(id);
        if (debour.isPresent())
            return debour.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Debour addOrUpdateDebour(
            Debour debour) {
        debour = debourRepository.save(debour);
        indexEntityService.indexEntity(debour, debour.getId());
        return debour;
    }

    @Override
    public List<DebourSearchResult> searchDebours(DebourSearch debourSearch) throws OsirisException {
        return debourRepository.findDebours(
                debourSearch.getCompetentAuthority() == null ? 0 : debourSearch.getCompetentAuthority().getId(),
                debourSearch.getCustomerOrder() == null ? 0 : debourSearch.getCustomerOrder().getEntityId(),
                debourSearch.getMaxAmount(),
                debourSearch.getMinAmount(), debourSearch.getIsNonAssociated(),
                debourSearch.getIsCompetentAuthorityDirectCharge(), constantService.getPaymentTypeAccount().getId());
    }

    @Override
    public void reindexDebours() {
        List<Debour> debours = IterableUtils.toList(debourRepository.findAll());
        if (debours != null)
            for (Debour debour : debours)
                indexEntityService.indexEntity(debour, debour.getId());
    }
}