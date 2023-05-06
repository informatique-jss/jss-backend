package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.MineurNationalite;
import com.jss.osiris.modules.quotation.repository.guichetUnique.MineurNationaliteRepository;

@Service
public class MineurNationaliteServiceImpl implements MineurNationaliteService {

    @Autowired
    MineurNationaliteRepository MineurNationaliteRepository;

    @Override
    public List<MineurNationalite> getMineurNationalite() {
        return IterableUtils.toList(MineurNationaliteRepository.findAll());
    }
}
