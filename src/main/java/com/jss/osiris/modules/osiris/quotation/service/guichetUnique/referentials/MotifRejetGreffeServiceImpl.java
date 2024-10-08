package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.MotifRejetGreffe;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.MotifRejetGreffeRepository;

@Service
public class MotifRejetGreffeServiceImpl implements MotifRejetGreffeService {

    @Autowired
    MotifRejetGreffeRepository MotifRejetGreffeRepository;

    @Override
    public List<MotifRejetGreffe> getMotifRejetGreffe() {
        return IterableUtils.toList(MotifRejetGreffeRepository.findAll());
    }
}
