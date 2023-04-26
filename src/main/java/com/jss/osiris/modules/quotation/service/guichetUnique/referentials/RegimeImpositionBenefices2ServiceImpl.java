package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.RegimeImpositionBenefices2;
import com.jss.osiris.modules.quotation.repository.guichetUnique.RegimeImpositionBenefices2Repository;

@Service
public class RegimeImpositionBenefices2ServiceImpl implements RegimeImpositionBenefices2Service {

    @Autowired
    RegimeImpositionBenefices2Repository RegimeImpositionBenefices2Repository;

    @Override
    public List<RegimeImpositionBenefices2> getRegimeImpositionBenefices2() {
        return IterableUtils.toList(RegimeImpositionBenefices2Repository.findAll());
    }
}
