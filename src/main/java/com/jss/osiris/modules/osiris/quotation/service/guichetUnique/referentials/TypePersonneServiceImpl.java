package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypePersonne;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.TypePersonneRepository;

@Service
public class TypePersonneServiceImpl implements TypePersonneService {

    @Autowired
    TypePersonneRepository TypePersonneRepository;

    @Override
    public List<TypePersonne> getTypePersonne() {
        return IterableUtils.toList(TypePersonneRepository.findAll());
    }
}