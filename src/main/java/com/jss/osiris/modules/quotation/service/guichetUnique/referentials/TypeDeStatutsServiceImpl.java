package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeDeStatuts;
import com.jss.osiris.modules.quotation.repository.guichetUnique.TypeDeStatutsRepository;

@Service
public class TypeDeStatutsServiceImpl implements TypeDeStatutsService {

    @Autowired
    TypeDeStatutsRepository TypeDeStatutsRepository;

    @Override
    public List<TypeDeStatuts> getTypeDeStatuts() {
        return IterableUtils.toList(TypeDeStatutsRepository.findAll());
    }
}
