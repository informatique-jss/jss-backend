package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.PrecisionActivite;
import com.jss.osiris.modules.quotation.repository.guichetUnique.PrecisionActiviteRepository;

@Service
public class PrecisionActiviteServiceImpl implements PrecisionActiviteService {

    @Autowired
    PrecisionActiviteRepository PrecisionActiviteRepository;

    @Override
    public List<PrecisionActivite> getPrecisionActivite() {
        return IterableUtils.toList(PrecisionActiviteRepository.findAll());
    }
}
