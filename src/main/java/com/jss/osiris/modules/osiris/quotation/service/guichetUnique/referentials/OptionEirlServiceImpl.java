package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.OptionEirl;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.OptionEirlRepository;

@Service
public class OptionEirlServiceImpl implements OptionEirlService {

    @Autowired
    OptionEirlRepository OptionEirlRepository;

    @Override
    public List<OptionEirl> getOptionEirl() {
        return IterableUtils.toList(OptionEirlRepository.findAll());
    }
}
