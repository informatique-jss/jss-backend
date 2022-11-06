package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.CodePays;
import com.jss.osiris.modules.quotation.repository.guichetUnique.CodePaysRepository;

@Service
public class CodePaysServiceImpl implements CodePaysService {

    @Autowired
    CodePaysRepository CodePaysRepository;

    @Override
    @Cacheable(value = "codePaysList", key = "#root.methodName")
    public List<CodePays> getCodePays() {
        return IterableUtils.toList(CodePaysRepository.findAll());
    }
}
