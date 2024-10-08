package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.DiffusionINSEE;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.DiffusionINSEERepository;

@Service
public class DiffusionINSEEServiceImpl implements DiffusionINSEEService {

    @Autowired
    DiffusionINSEERepository DiffusionINSEERepository;

    @Override
    public List<DiffusionINSEE> getDiffusionINSEE() {
        return IterableUtils.toList(DiffusionINSEERepository.findAll());
    }
}
