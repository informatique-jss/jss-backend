package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.OptionEirl;
import com.jss.osiris.modules.quotation.repository.guichetUnique.OptionEirlRepository;

@Service
public class OptionEirlServiceImpl implements OptionEirlService {

    @Autowired
    OptionEirlRepository OptionEirlRepository;

    @Override
    public List<OptionEirl> getOptionEirl() {
        return IterableUtils.toList(OptionEirlRepository.findAll());
    }
}
