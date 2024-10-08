package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.RegistreEirl;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.RegistreEirlRepository;

@Service
public class RegistreEirlServiceImpl implements RegistreEirlService {

    @Autowired
    RegistreEirlRepository RegistreEirlRepository;

    @Override
    public List<RegistreEirl> getRegistreEirl() {
        return IterableUtils.toList(RegistreEirlRepository.findAll());
    }
}
