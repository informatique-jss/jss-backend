package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypeDePersonne;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.TypeDePersonneRepository;

@Service
public class TypeDePersonneServiceImpl implements TypeDePersonneService {

    @Autowired
    TypeDePersonneRepository TypeDePersonneRepository;

    @Override
    public List<TypeDePersonne> getTypeDePersonne() {
        return IterableUtils.toList(TypeDePersonneRepository.findAll());
    }
}
