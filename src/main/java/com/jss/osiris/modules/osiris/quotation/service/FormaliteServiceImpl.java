package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.Formalite;
import com.jss.osiris.modules.osiris.quotation.repository.FormaliteRepository;

@Service
public class FormaliteServiceImpl implements FormaliteService {

    @Autowired
    FormaliteRepository formaliteRepository;

    @Autowired
    ConstantService constantService;

    @Autowired
    CustomerOrderStatusService customerOrderStatusService;

    @Autowired
    FormaliteStatusService formaliteStatusService;

    @Override
    public Formalite getFormalite(Integer id) {
        Optional<Formalite> formalite = formaliteRepository.findById(id);
        if (formalite.isPresent())
            return formalite.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Formalite addOrUpdateFormalite(Formalite formalite) {
        return formaliteRepository.save(formalite);
    }

    @Override
    public List<Formalite> getFormaliteForGURefresh() throws OsirisException {
        return formaliteRepository.getFormaliteForGURefresh(
                customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.BILLED).getId());
    }
}
