package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeRepresentant;
import com.jss.osiris.modules.quotation.repository.guichetUnique.TypeRepresentantRepository;

@Service
public class TypeRepresentantServiceImpl implements TypeRepresentantService {

    @Autowired
    TypeRepresentantRepository TypeRepresentantRepository;

    @Override
    public List<TypeRepresentant> getTypeRepresentant() {
        return IterableUtils.toList(TypeRepresentantRepository.findAll());
    }
}
