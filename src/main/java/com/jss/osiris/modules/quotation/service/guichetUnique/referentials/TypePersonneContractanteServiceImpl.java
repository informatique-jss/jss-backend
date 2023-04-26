package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypePersonneContractante;
import com.jss.osiris.modules.quotation.repository.guichetUnique.TypePersonneContractanteRepository;

@Service
public class TypePersonneContractanteServiceImpl implements TypePersonneContractanteService {

    @Autowired
    TypePersonneContractanteRepository TypePersonneContractanteRepository;

    @Override
    public List<TypePersonneContractante> getTypePersonneContractante() {
        return IterableUtils.toList(TypePersonneContractanteRepository.findAll());
    }
}
