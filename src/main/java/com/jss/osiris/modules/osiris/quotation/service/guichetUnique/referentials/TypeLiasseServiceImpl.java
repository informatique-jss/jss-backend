package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypeLiasse;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.TypeLiasseRepository;

@Service
public class TypeLiasseServiceImpl implements TypeLiasseService {

    @Autowired
    TypeLiasseRepository TypeLiasseRepository;

    @Override
    public List<TypeLiasse> getTypeLiasse() {
        return IterableUtils.toList(TypeLiasseRepository.findAll());
    }
}
