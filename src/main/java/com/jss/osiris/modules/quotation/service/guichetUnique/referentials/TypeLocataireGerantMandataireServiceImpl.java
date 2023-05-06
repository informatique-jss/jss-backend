package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeLocataireGerantMandataire;
import com.jss.osiris.modules.quotation.repository.guichetUnique.TypeLocataireGerantMandataireRepository;

@Service
public class TypeLocataireGerantMandataireServiceImpl implements TypeLocataireGerantMandataireService {

    @Autowired
    TypeLocataireGerantMandataireRepository TypeLocataireGerantMandataireRepository;

    @Override
    public List<TypeLocataireGerantMandataire> getTypeLocataireGerantMandataire() {
        return IterableUtils.toList(TypeLocataireGerantMandataireRepository.findAll());
    }
}
