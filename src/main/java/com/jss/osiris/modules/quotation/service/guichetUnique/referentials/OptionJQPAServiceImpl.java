package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.OptionJQPA;
import com.jss.osiris.modules.quotation.repository.guichetUnique.OptionJQPARepository;

@Service
public class OptionJQPAServiceImpl implements OptionJQPAService {

    @Autowired
    OptionJQPARepository OptionJQPARepository;

    @Override
    public List<OptionJQPA> getOptionJQPA() {
        return IterableUtils.toList(OptionJQPARepository.findAll());
    }
}
