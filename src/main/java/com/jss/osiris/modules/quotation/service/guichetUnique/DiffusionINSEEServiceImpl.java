package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.DiffusionINSEE;
import com.jss.osiris.modules.quotation.repository.guichetUnique.DiffusionINSEERepository;

@Service
public class DiffusionINSEEServiceImpl implements DiffusionINSEEService {

    @Autowired
    DiffusionINSEERepository DiffusionINSEERepository;

    @Override
    @Cacheable(value = "diffusionINSEEList", key = "#root.methodName")
    public List<DiffusionINSEE> getDiffusionINSEE() {
        return IterableUtils.toList(DiffusionINSEERepository.findAll());
    }
}
