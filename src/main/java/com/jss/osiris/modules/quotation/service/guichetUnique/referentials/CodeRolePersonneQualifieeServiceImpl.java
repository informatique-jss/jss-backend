package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.CodeRolePersonneQualifiee;
import com.jss.osiris.modules.quotation.repository.guichetUnique.CodeRolePersonneQualifieeRepository;

@Service
public class CodeRolePersonneQualifieeServiceImpl implements CodeRolePersonneQualifieeService {

    @Autowired
    CodeRolePersonneQualifieeRepository CodeRolePersonneQualifieeRepository;

    @Override
    @Cacheable(value = "codeRolePersonneQualifieeList", key = "#root.methodName")
    public List<CodeRolePersonneQualifiee> getCodeRolePersonneQualifiee() {
        return IterableUtils.toList(CodeRolePersonneQualifieeRepository.findAll());
    }
}
