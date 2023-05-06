package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeDissolution;
import com.jss.osiris.modules.quotation.repository.guichetUnique.TypeDissolutionRepository;

@Service
public class TypeDissolutionServiceImpl implements TypeDissolutionService {

    @Autowired
    TypeDissolutionRepository TypeDissolutionRepository;

    @Override
    public List<TypeDissolution> getTypeDissolution() {
        return IterableUtils.toList(TypeDissolutionRepository.findAll());
    }
}
