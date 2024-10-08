package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.CodePays;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.CodePaysRepository;

@Service
public class CodePaysServiceImpl implements CodePaysService {

    @Autowired
    CodePaysRepository CodePaysRepository;

    @Override
    public List<CodePays> getCodePays() {
        return IterableUtils.toList(CodePaysRepository.findAll());
    }
}
