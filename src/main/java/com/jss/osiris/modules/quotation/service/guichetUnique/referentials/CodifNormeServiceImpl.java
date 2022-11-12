package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.CodifNorme;
import com.jss.osiris.modules.quotation.repository.guichetUnique.CodifNormeRepository;

@Service
public class CodifNormeServiceImpl implements CodifNormeService {

    @Autowired
    CodifNormeRepository CodifNormeRepository;

    @Override
    @Cacheable(value = "codifNormeList", key = "#root.methodName")
    public List<CodifNorme> getCodifNorme() {
        return IterableUtils.toList(CodifNormeRepository.findAll());
    }
}
