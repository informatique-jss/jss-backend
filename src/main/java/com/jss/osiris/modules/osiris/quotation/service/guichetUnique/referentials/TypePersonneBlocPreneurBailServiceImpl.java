package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypePersonneBlocPreneurBail;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.TypePersonneBlocPreneurBailRepository;

@Service
public class TypePersonneBlocPreneurBailServiceImpl implements TypePersonneBlocPreneurBailService {

    @Autowired
    TypePersonneBlocPreneurBailRepository TypePersonneBlocPreneurBailRepository;

    @Override
    public List<TypePersonneBlocPreneurBail> getTypePersonneBlocPreneurBail() {
        return IterableUtils.toList(TypePersonneBlocPreneurBailRepository.findAll());
    }
}
