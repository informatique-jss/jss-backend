package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.CodeInseePays;
import com.jss.osiris.modules.quotation.repository.guichetUnique.CodeInseePaysRepository;

@Service
public class CodeInseePaysServiceImpl implements CodeInseePaysService {

    @Autowired
    CodeInseePaysRepository CodeInseePaysRepository;

    @Override
    public List<CodeInseePays> getCodeInseePays() {
        return IterableUtils.toList(CodeInseePaysRepository.findAll());
    }
}
