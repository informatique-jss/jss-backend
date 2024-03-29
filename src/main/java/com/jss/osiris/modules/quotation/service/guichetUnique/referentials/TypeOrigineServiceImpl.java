package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeOrigine;
import com.jss.osiris.modules.quotation.repository.guichetUnique.TypeOrigineRepository;

@Service
public class TypeOrigineServiceImpl implements TypeOrigineService {

    @Autowired
    TypeOrigineRepository TypeOrigineRepository;

    @Override
    public List<TypeOrigine> getTypeOrigine() {
        return IterableUtils.toList(TypeOrigineRepository.findAll());
    }
}
