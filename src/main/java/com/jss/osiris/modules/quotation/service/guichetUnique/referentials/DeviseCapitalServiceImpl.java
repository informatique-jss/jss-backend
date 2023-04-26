package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.DeviseCapital;
import com.jss.osiris.modules.quotation.repository.guichetUnique.DeviseCapitalRepository;

@Service
public class DeviseCapitalServiceImpl implements DeviseCapitalService {

    @Autowired
    DeviseCapitalRepository DeviseCapitalRepository;

    @Override
    public List<DeviseCapital> getDeviseCapital() {
        return IterableUtils.toList(DeviseCapitalRepository.findAll());
    }
}
