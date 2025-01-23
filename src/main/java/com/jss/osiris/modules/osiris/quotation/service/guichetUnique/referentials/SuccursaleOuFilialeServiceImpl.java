package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.SuccursaleOuFiliale;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.SuccursaleOuFilialeRepository;

@Service
public class SuccursaleOuFilialeServiceImpl implements SuccursaleOuFilialeService {

    @Autowired
    SuccursaleOuFilialeRepository SuccursaleOuFilialeRepository;

    @Override
    public List<SuccursaleOuFiliale> getSuccursaleOuFiliale() {
        return IterableUtils.toList(SuccursaleOuFilialeRepository.findAll());
    }
}
