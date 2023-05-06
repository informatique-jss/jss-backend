package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.CodeInseePaysNaissance;
import com.jss.osiris.modules.quotation.repository.guichetUnique.CodeInseePaysNaissanceRepository;

@Service
public class CodeInseePaysNaissanceServiceImpl implements CodeInseePaysNaissanceService {

    @Autowired
    CodeInseePaysNaissanceRepository CodeInseePaysNaissanceRepository;

    @Override
    public List<CodeInseePaysNaissance> getCodeInseePaysNaissance() {
        return IterableUtils.toList(CodeInseePaysNaissanceRepository.findAll());
    }
}
