package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypeVoie;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.TypeVoieRepository;

@Service
public class TypeVoieServiceImpl implements TypeVoieService {

    @Autowired
    TypeVoieRepository TypeVoieRepository;

    @Override
    public List<TypeVoie> getTypeVoie() {
        return IterableUtils.toList(TypeVoieRepository.findAll());
    }

    @Override
    public TypeVoie getTypeVoie(String code) {
        Optional<TypeVoie> typeVoie = TypeVoieRepository.findById(code);
        if (typeVoie.isPresent())
            return typeVoie.get();
        return null;
    }
}
