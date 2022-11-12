package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.CodeNationalite;
import com.jss.osiris.modules.quotation.repository.guichetUnique.CodeNationaliteRepository;

@Service
public class CodeNationaliteServiceImpl implements CodeNationaliteService {

    @Autowired
    CodeNationaliteRepository CodeNationaliteRepository;

    @Override
    @Cacheable(value = "codeNationaliteList", key = "#root.methodName")
    public List<CodeNationalite> getCodeNationalite() {
        return IterableUtils.toList(CodeNationaliteRepository.findAll());
    }
}
