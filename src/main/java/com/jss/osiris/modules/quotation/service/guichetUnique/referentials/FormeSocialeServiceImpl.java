package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.FormeSociale;
import com.jss.osiris.modules.quotation.repository.guichetUnique.FormeSocialeRepository;

@Service
public class FormeSocialeServiceImpl implements FormeSocialeService {

    @Autowired
    FormeSocialeRepository FormeSocialeRepository;

    @Override
    public List<FormeSociale> getFormeSociale() {
        return IterableUtils.toList(FormeSocialeRepository.findAll());
    }
}
