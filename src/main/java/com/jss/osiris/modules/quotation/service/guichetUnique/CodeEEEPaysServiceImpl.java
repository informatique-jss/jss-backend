package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.CodeEEEPays;
import com.jss.osiris.modules.quotation.repository.guichetUnique.CodeEEEPaysRepository;

@Service
public class CodeEEEPaysServiceImpl implements CodeEEEPaysService {

    @Autowired
    CodeEEEPaysRepository CodeEEEPaysRepository;

    @Override
    @Cacheable(value = "codeEEEPaysList", key = "#root.methodName")
    public List<CodeEEEPays> getCodeEEEPays() {
        return IterableUtils.toList(CodeEEEPaysRepository.findAll());
    }
}
