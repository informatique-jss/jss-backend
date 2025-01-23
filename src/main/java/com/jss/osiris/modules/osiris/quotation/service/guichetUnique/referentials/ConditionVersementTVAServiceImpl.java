package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.ConditionVersementTVA;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.ConditionVersementTVARepository;

@Service
public class ConditionVersementTVAServiceImpl implements ConditionVersementTVAService {

    @Autowired
    ConditionVersementTVARepository ConditionVersementTVARepository;

    @Override
    public List<ConditionVersementTVA> getConditionVersementTVA() {
        return IterableUtils.toList(ConditionVersementTVARepository.findAll());
    }
}
