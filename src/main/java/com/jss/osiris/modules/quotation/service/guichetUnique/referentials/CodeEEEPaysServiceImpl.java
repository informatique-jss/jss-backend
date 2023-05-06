package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.CodeEEEPays;
import com.jss.osiris.modules.quotation.repository.guichetUnique.CodeEEEPaysRepository;

@Service
public class CodeEEEPaysServiceImpl implements CodeEEEPaysService {

    @Autowired
    CodeEEEPaysRepository CodeEEEPaysRepository;

    @Override
    public List<CodeEEEPays> getCodeEEEPays() {
        return IterableUtils.toList(CodeEEEPaysRepository.findAll());
    }
}
